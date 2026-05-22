#!/usr/bin/env python3
"""
Build seed.db from a directory of Karakalpak text files.

Supported input formats: .txt, .pdf, .docx, .doc
Output: seed.db with seed_words table (word, script, frequency)

Usage:
    python build_seed_db.py --input <texts_dir> --output <path/to/seed.db>

Options:
    --input       Directory containing source text files (default: ./texts)
    --output      Output SQLite file path (default: ../app/src/main/assets/seed.db)
    --min-freq    Minimum frequency threshold (default: 3)
    --min-len     Minimum word length (default: 3)
"""

import argparse
import re
import sqlite3
import unicodedata
from collections import Counter
from pathlib import Path


# ---------------------------------------------------------------------------
# Script detection
# ---------------------------------------------------------------------------

CYRILLIC_PATTERN = re.compile(r'[Ѐ-ӿ]')
LATIN_PATTERN = re.compile(r'[a-zA-ZÀ-ɏ]')
DIGIT_PATTERN = re.compile(r'\d')


def detect_script(word: str) -> str | None:
    """Return 'latin', 'cyrillic', or None if mixed/undetermined."""
    has_cyrillic = bool(CYRILLIC_PATTERN.search(word))
    has_latin = bool(LATIN_PATTERN.search(word))
    if has_cyrillic and not has_latin:
        return 'cyrillic'
    if has_latin and not has_cyrillic:
        return 'latin'
    return None  # mixed or neither


# ---------------------------------------------------------------------------
# Text extraction
# ---------------------------------------------------------------------------

def extract_txt(path: Path) -> str:
    try:
        return path.read_text(encoding='utf-8', errors='ignore')
    except Exception as e:
        print(f"  [warn] Could not read {path.name}: {e}")
        return ''


def extract_pdf(path: Path) -> str:
    try:
        import pdfplumber
        text_parts = []
        with pdfplumber.open(path) as pdf:
            for page in pdf.pages:
                t = page.extract_text()
                if t:
                    text_parts.append(t)
        return '\n'.join(text_parts)
    except ImportError:
        print("  [warn] pdfplumber not installed. Run: pip install pdfplumber")
        return ''
    except Exception as e:
        print(f"  [warn] Could not read PDF {path.name}: {e}")
        return ''


def extract_docx(path: Path) -> str:
    try:
        from docx import Document
        doc = Document(path)
        return '\n'.join(p.text for p in doc.paragraphs)
    except ImportError:
        print("  [warn] python-docx not installed. Run: pip install python-docx")
        return ''
    except Exception as e:
        print(f"  [warn] Could not read DOCX {path.name}: {e}")
        return ''


def extract_doc(path: Path) -> str:
    """Legacy .doc -- requires antiword or libreoffice on PATH."""
    import subprocess
    for tool in ('antiword', 'libreoffice'):
        try:
            if tool == 'antiword':
                result = subprocess.run(
                    ['antiword', str(path)], capture_output=True, text=True, timeout=30
                )
                if result.returncode == 0:
                    return result.stdout
            else:
                result = subprocess.run(
                    ['libreoffice', '--headless', '--convert-to', 'txt', '--outdir', '/tmp', str(path)],
                    capture_output=True, timeout=60
                )
                tmp = Path('/tmp') / (path.stem + '.txt')
                if tmp.exists():
                    text = tmp.read_text(encoding='utf-8', errors='ignore')
                    tmp.unlink()
                    return text
        except (FileNotFoundError, subprocess.TimeoutExpired):
            continue
    print(f"  [warn] Could not extract .doc {path.name} -- install antiword or libreoffice")
    return ''


EXTRACTORS = {
    '.txt': extract_txt,
    '.pdf': extract_pdf,
    '.docx': extract_docx,
    '.doc': extract_doc,
}


def extract_text(path: Path) -> str:
    ext = path.suffix.lower()
    extractor = EXTRACTORS.get(ext)
    if extractor is None:
        return ''
    print(f"  Reading {path.name} ({ext})")
    return extractor(path)


# ---------------------------------------------------------------------------
# Tokenization and normalization
# ---------------------------------------------------------------------------

# Characters to strip from word boundaries
STRIP_CHARS = (
    '.,!?;:()[]{}"\''
    + '“”'   # curved double quotes
    + '‘’'   # curved single quotes
    + '«»'   # << >>
    + '—–'   # em dash, en dash
    + '…'         # ellipsis
    + '/\\'
)

# Exact Karakalpak Latin character set from the keyboard layout (lowercase only; input is lowercased first)
VALID_LATIN_CHARS = frozenset(
    'abcdefghijklmnopqrstuvwxyz'
    + 'á'   # a-acute (a with acute)
    + 'ǵ'   # g-acute (g with acute)
    + 'ı'   # dotless i
    + 'ń'   # n-acute
    + 'ó'   # o-acute
    + 'ú'   # u-acute
    + '-'
)


def normalize_word(word: str) -> str:
    word = word.lower().strip(STRIP_CHARS)
    return unicodedata.normalize('NFC', word)


def tokenize(text: str) -> list[str]:
    return text.split()


def is_valid_word(word: str, script: str, min_len: int) -> bool:
    if len(word) < min_len:
        return False

    if DIGIT_PATTERN.search(word):
        return False

    # Rule 1: must start and end with a letter
    if not (word[0].isalpha() and word[-1].isalpha()):
        return False

    # Rule 2: only letters and dashes -- no other symbols allowed
    if not all(c.isalpha() or c == '-' for c in word):
        return False

    # Rule 3: all letters must belong to the detected script's valid set
    if script == 'latin':
        if not all(c in VALID_LATIN_CHARS for c in word):
            return False
    elif script == 'cyrillic':
        if not all('Ѐ' <= c <= 'ӿ' or c == '-' for c in word):
            return False

    return True


# ---------------------------------------------------------------------------
# Main pipeline
# ---------------------------------------------------------------------------

def collect_words(input_dir: Path, min_len: int) -> Counter:
    counts: Counter = Counter()
    files = [
        p for p in input_dir.rglob('*')
        if p.is_file() and p.suffix.lower() in EXTRACTORS
    ]
    if not files:
        print(f"No supported files found in {input_dir}")
        return counts

    print(f"Found {len(files)} file(s) to process")
    for path in files:
        text = extract_text(path)
        if not text:
            continue
        for raw_token in tokenize(text):
            word = normalize_word(raw_token)
            script = detect_script(word)
            if script is None:
                continue
            if not is_valid_word(word, script, min_len):
                continue
            counts[(word, script)] += 1

    return counts


def write_db(counts: Counter, output_path: Path, min_freq: int) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)

    if output_path.exists():
        output_path.unlink()

    conn = sqlite3.connect(output_path)
    cur = conn.cursor()

    cur.execute("""
        CREATE TABLE seed_words (
            word      TEXT,
            script    TEXT,
            frequency INTEGER DEFAULT 1,
            PRIMARY KEY (word, script)
        )
    """)
    cur.execute('CREATE INDEX idx_seed_word ON seed_words(word, script)')

    rows = [
        (word, script, freq)
        for (word, script), freq in counts.items()
        if freq >= min_freq
    ]
    rows.sort(key=lambda r: r[2], reverse=True)

    cur.executemany(
        'INSERT INTO seed_words (word, script, frequency) VALUES (?, ?, ?)',
        rows
    )
    conn.commit()
    conn.close()

    latin_count = sum(1 for _, s, _ in rows if s == 'latin')
    cyrillic_count = sum(1 for _, s, _ in rows if s == 'cyrillic')
    print(f"\nWrote {len(rows)} words to {output_path}")
    print(f"  Latin:    {latin_count}")
    print(f"  Cyrillic: {cyrillic_count}")


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

def main():
    default_output = Path(__file__).parent.parent / 'app' / 'src' / 'main' / 'assets' / 'seed.db'

    parser = argparse.ArgumentParser(description='Build Karakalpak seed dictionary')
    parser.add_argument('--input', default='texts', help='Directory of source text files')
    parser.add_argument('--output', default=str(default_output), help='Output seed.db path')
    parser.add_argument('--min-freq', type=int, default=3, help='Minimum word frequency')
    parser.add_argument('--min-len', type=int, default=3, help='Minimum word length')
    args = parser.parse_args()

    input_dir = Path(args.input)
    output_path = Path(args.output)

    if not input_dir.exists():
        print(f"Input directory not found: {input_dir}")
        return

    print(f"Input:    {input_dir.resolve()}")
    print(f"Output:   {output_path.resolve()}")
    print(f"Min freq: {args.min_freq}")
    print(f"Min len:  {args.min_len}\n")

    counts = collect_words(input_dir, args.min_len)
    if not counts:
        print("No words collected -- check your input files.")
        return

    write_db(counts, output_path, args.min_freq)


if __name__ == '__main__':
    main()
