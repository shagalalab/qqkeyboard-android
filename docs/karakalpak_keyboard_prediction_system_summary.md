# Karakalpak Android Keyboard — Autosuggestion & Prediction System Summary

## Overview
This document summarizes the discussed architecture and implementation ideas for building autosuggestion and next-word prediction features for a custom Android keyboard targeting the Karakalpak language.

The primary challenge is that Karakalpak is a low-resource language with limited existing dictionaries and datasets. Therefore, the system should:

- Work with a very small initial dictionary.
- Learn dynamically from user input.
- Preserve user privacy.
- Improve over time locally and optionally globally.

---

# 1. Basic Autosuggestion Architecture

## Core Idea
The keyboard learns from:

1. A small preloaded seed dictionary.
2. User typing behavior.

The system continuously improves by collecting word usage frequencies locally.

---

# 2. Hybrid Dictionary Design

## Recommended Structure
Keep:

- A static seed dictionary.
- A dynamic user dictionary.

Separate.

This is preferable to merging everything into one table.

---

## Why Keep Them Separate?

### Advantages
- Keeps the seed dictionary clean and curated.
- Allows clearing user-learned data independently.
- Makes seed dictionary updates easier.
- Prevents noisy user data from polluting the base dictionary.
- Enables different ranking strategies for seed vs user words.

---

# 3. Database Schema

## Seed Dictionary (Read-only)

```sql
CREATE TABLE seed_words (
    word TEXT PRIMARY KEY,
    frequency INTEGER DEFAULT 1,
    priority INTEGER DEFAULT 1
);
```

---

## User Dictionary (Dynamic)

```sql
CREATE TABLE user_words (
    word TEXT PRIMARY KEY,
    frequency INTEGER DEFAULT 1,
    last_used INTEGER,
    synced INTEGER DEFAULT 0
);
```

---

# 4. Autosuggestion Flow

## Word Learning
Whenever the user commits a word (space, enter, punctuation):

1. Normalize the word.
2. Ignore invalid tokens:
   - Numbers
   - Very short tokens
   - Password fields
3. Update `user_words`:
   - Insert if new.
   - Increment frequency if existing.

---

## Prefix Suggestions
When user types:

```text
qar
```

The keyboard performs prefix searches:

### User Dictionary Query

```sql
SELECT word, frequency
FROM user_words
WHERE word LIKE 'qar%'
ORDER BY frequency DESC
LIMIT 10;
```

### Seed Dictionary Query

```sql
SELECT word, frequency
FROM seed_words
WHERE word LIKE 'qar%'
LIMIT 10;
```

---

## Merging Results
Merge both result sets in memory.

Suggested ranking:

```text
score = user_frequency * 2 + seed_frequency
```

User-learned words should generally rank higher.

---

# 5. Privacy Considerations

The keyboard should:

- Never store passwords.
- Respect Android flags such as:
  - `TYPE_TEXT_VARIATION_PASSWORD`
  - `IME_FLAG_NO_PERSONALIZED_LEARNING`
- Avoid logging complete sentences.
- Keep all learning local unless user explicitly opts in.

---

# 6. Optional Sync with Server

## Goal
Improve the global Karakalpak dictionary using anonymous community contributions.

---

## Recommended Sync Strategy

### User Consent
Only sync if user explicitly opts in.

Example setting:

```text
Help improve Karakalpak predictions
```

---

## What to Upload
Recommended payload:

```json
{
  "language": "kaa",
  "device_id": "hashed_device_id",
  "words": [
    { "w": "qarindash", "f": 18 },
    { "w": "arqaly", "f": 12 }
  ]
}
```

Only upload:

- Words
- Frequencies
- Optional timestamps

Avoid uploading full sentences.

---

## Sync Rules
Recommended:

- Upload only words with frequency >= 3.
- Sync periodically.
- Mark rows as synced afterward.

Example:

```sql
UPDATE user_words
SET synced = 1
WHERE word IN (...);
```

---

# 7. Server-Side Aggregation

## Aggregation Table

```sql
CREATE TABLE global_words (
    word TEXT PRIMARY KEY,
    total_frequency INTEGER,
    user_count INTEGER,
    last_seen INTEGER
);
```

---

## Aggregation Logic

Server should:

1. Sum frequencies.
2. Count unique users.
3. Filter rare words.
4. Remove noise/spam.
5. Add common words into future seed dictionary versions.

---

# 8. Next-Word Prediction

## Goal
Predict the next word after the user finishes typing the current one.

Example:

```text
men baraman -> ertenge
```

---

# 9. Recommended Initial Approach

## Use Statistical Models First
Instead of neural networks initially:

- Use bigrams.
- Optionally trigrams later.

This approach is:

- Simple
- Lightweight
- Fast
- Works well for low-resource languages

---

# 10. Bigram Model

## Data Structure

```sql
CREATE TABLE next_word_model (
    prefix TEXT,
    next_word TEXT,
    frequency INTEGER,
    last_used INTEGER,
    PRIMARY KEY(prefix, next_word)
);
```

---

# 11. Training the Bigram Model

Whenever user types:

```text
men baraman ertenge
```

Generate:

```text
men -> baraman
baraman -> ertenge
```

Increment frequency counts.

---

# 12. Next-Word Prediction Query

Example:

```sql
SELECT next_word
FROM next_word_model
WHERE prefix = 'baraman'
ORDER BY frequency DESC
LIMIT 3;
```

If no match exists:

Fallback to globally frequent words.

---

# 13. Hybrid Suggestion UI

The keyboard can combine:

## Current Word Suggestions
Based on:

- Prefix matching
- Dictionary lookup

Example:

```text
qar -> qarindash
```

---

## Next Word Predictions
Based on:

- Bigram/trigram frequencies

Example:

```text
men -> baraman
```

---

# 14. Future Upgrade Path

Once sufficient data exists:

1. Train a small offline neural language model.
2. Ship it with the app.
3. Keep local personalization.
4. Optionally adopt federated learning later.

---

# 15. Federated Learning (Advanced)

## Concept
Instead of sending words to the server:

- Train a language model locally.
- Upload only model weight updates.
- Aggregate updates server-side.

This improves privacy because raw text never leaves the device.

---

## Challenges
- More complex implementation.
- Requires ML infrastructure.
- More battery and compute usage.
- Harder for small projects.

---

# 16. Recommended Development Roadmap

## Phase 1 — MVP
- Small seed dictionary.
- Local user dictionary.
- Prefix suggestions.
- Frequency ranking.

---

## Phase 2 — Smarter Suggestions
- Add bigram next-word prediction.
- Add typo tolerance.
- Add recency weighting.

---

## Phase 3 — Community Improvement
- Opt-in sync.
- Aggregate anonymous word statistics.
- Improve seed dictionary.

---

## Phase 4 — Advanced ML
- Tiny local neural model.
- Federated learning.
- Personalized language adaptation.

---

# 17. Overall Recommendation

For a Karakalpak keyboard, the best practical approach is:

1. Start simple.
2. Use lightweight statistical methods.
3. Learn locally from user behavior.
4. Gradually grow the ecosystem.
5. Introduce ML only after sufficient data exists.

This approach is realistic, scalable, privacy-friendly, and well-suited for a low-resource language ecosystem.

