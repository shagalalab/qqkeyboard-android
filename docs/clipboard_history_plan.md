# Clipboard History — Implementation Plan

Gboard-style multi-clipboard for QqKeyboard: the keyboard remembers recently copied
text and offers it back as tappable cards, with the option to pin the ones worth
keeping.

## Decisions already made

| Decision | Choice |
| --- | --- |
| Entry point | Clipboard icon at the **start** of the suggestion strip, balancing the emoji icon at the end |
| Availability | Clipboard is **off entirely** when the suggestion strip is off — no button, no capture, no storage |
| Retention | Unpinned clips expire **1 hour** after being copied; pinned clips are kept indefinitely |
| Content types | **Text only** (no images) |

---

## 1. Scope

**In scope**

- Capture plain-text clips copied anywhere on the device while QqKeyboard is the
  active input method.
- A clipboard panel that overlays the key area, exactly the way `EmojiLayout` does today.
- Tap a clip to paste it at the cursor.
- Long-press a clip to pin / unpin / delete it.
- 1-hour expiry for unpinned clips, pinned clips kept until deleted.
- Karakalpak strings for all new UI.

**Out of scope** (worth revisiting later, listed in §8)

- Image or rich-content clips.
- Manually adding a clip from inside the keyboard (Gboard's "+" button).
- A "paste" chip appearing in the suggestion strip right after a copy.
- Syncing clips across devices.

---

## 2. What Android permits

There is no permission to declare, but there are real platform constraints that
shape the design:

- **Reading the clipboard is restricted from Android 10 (API 29).**
  `ClipboardManager.getPrimaryClip()` returns `null` unless the caller has window
  focus **or is the current default IME**. QqKeyboard qualifies through the IME
  exemption, but only while it actually is the user's selected keyboard — so
  every read must tolerate a `null` result rather than assume success.
- **Some OEM builds throw `SecurityException`** from `getPrimaryClip()` instead of
  returning `null`. Every read goes in a `try/catch`; a failure means "no clip
  this time", never a crash of the IME process.
- **`OnPrimaryClipChangedListener` is gated by the same rule.** The framework
  checks read access before dispatching to each listener, so callbacks can
  silently stop arriving. This is why capture uses **two paths** (§4.2): the
  listener as the fast path, and a poll on `onStartInputView` as the reliable one.
- **`ClipDescription.getTimestamp()` exists since API 26**, which is our `minSdk`.
  It gives an authoritative "when was this put on the clipboard", which is a much
  better freshness check than comparing text, and it makes the two capture paths
  idempotent for free.
- **Sensitive clips.** `ClipDescription.EXTRA_IS_SENSITIVE` (API 33) marks
  password-manager and OTP content. The extras bundle itself is readable since
  API 24, and some apps set the key on older releases, so read it by string
  constant rather than gating on the API level.
- **Clipboard-access toasts** (Android 12+) are suppressed by the platform for the
  default IME. This is worth confirming on a real device during Stage 1 rather
  than taking on trust — if a toast does appear on every keyboard open, the
  listener-only path becomes the better default.

---

## 3. Data layer

### 3.1 Model — `keyboard/model/ClipItem.kt`

```kotlin
@Immutable
data class ClipItem(
    val id: Long,
    val text: String,
    val copiedAt: Long,
    val pinned: Boolean,
)
```

### 3.2 Storage — `keyboard/data/db/ClipboardDatabase.kt`

SQLite via `SQLiteOpenHelper`, matching the existing `UserDictionary` pattern in
the same package. SharedPreferences JSON (as `recentEmojis` uses) was the
alternative, but clips are far bigger than emoji — a 50-entry history is easily
100 KB, and SharedPreferences keeps its whole file resident in memory for the
life of the IME process. Expiry and pruning are also single `DELETE`s in SQL.

```sql
CREATE TABLE clips (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    text      TEXT    NOT NULL,
    copied_at INTEGER NOT NULL,  -- ClipDescription.getTimestamp(), else now()
    pinned    INTEGER NOT NULL DEFAULT 0,
    pinned_at INTEGER            -- orders the pinned section; null when unpinned
);
CREATE UNIQUE INDEX idx_clip_text ON clips(text);
```

The unique index on `text` is what makes re-copying the same string bump it back
to the top instead of creating a duplicate. Insert follows the same
`insertWithOnConflict(CONFLICT_IGNORE)` → `UPDATE` shape as
`UserDictionary.learnWord`, so a re-copy refreshes `copied_at` **without**
clearing `pinned` — a `REPLACE` would silently unpin things.

Constants:

| Name | Value | Purpose |
| --- | --- | --- |
| `UNPINNED_TTL_MS` | `3_600_000` (1 h) | Expiry window for unpinned clips |
| `MAX_UNPINNED` | `50` | Backstop cap; expiry does the real work |
| `MAX_PINNED` | `100` | Soft cap so the panel stays navigable |
| `MAX_CLIP_LENGTH` | `20_000` | Longer clips are skipped, not truncated — a truncated paste would be worse than no entry |

Expiry is enforced **both** on write (a `DELETE` on every capture and panel open)
and in the read query's `WHERE` clause. The read-side filter is what guarantees a
stale clip can never be displayed even if a purge hasn't run.

### 3.3 Repository — `keyboard/data/ClipboardRepository.kt`

Mirrors `SuggestionRepository`. All methods are `suspend` and run on
`Dispatchers.IO`; the ViewModel never touches the DB on the main thread.

```kotlin
suspend fun clips(): List<ClipItem>            // pinned by pinned_at DESC, then copied_at DESC
suspend fun capture(text: String, copiedAt: Long)
suspend fun setPinned(id: Long, pinned: Boolean)
suspend fun delete(id: Long)
suspend fun clearAll()
```

---

## 4. Capture

### 4.1 When capture is allowed

Capture is skipped entirely unless **all** of these hold:

1. The suggestion strip is enabled (`suggestionStripEnabled`) — per the decision
   that strip-off means clipboard-off.
2. The focused field is not a password field and not one of the numeric/phone
   special layouts. `QqKeyboard` already computes this as `isSpecialLayout`; that
   predicate moves onto the ViewModel so capture and the strip's visibility share
   one source of truth instead of duplicating the condition.
3. `EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING` is not set — the existing
   `isSuggestionsAllowed()` check already encodes this and can be reused.
4. The clip is not flagged `EXTRA_IS_SENSITIVE`.
5. The clip's MIME type is `text/plain` or `text/html`, and `item.text` is
   non-blank. Reading `item.text` directly rather than `coerceToText()` avoids
   firing a ContentResolver query from the IME process for URI-backed items.
6. The text is at most `MAX_CLIP_LENGTH` characters.

Turning the suggestion strip off does **not** wipe existing history. Pinned clips
in particular shouldn't disappear because of a UI toggle, and unpinned ones
expire within the hour regardless.

### 4.2 The two capture paths

Both funnel into one ViewModel entry point, and `copied_at` dedupes them:

- **Listener (fast path).** `QqKeyboardService` registers an
  `OnPrimaryClipChangedListener` in `onCreate` and removes it in `onDestroy`.
  Fires while the keyboard is open so a clip copied mid-session appears
  immediately.
- **Poll (reliable path).** `onStartInputView` reads `getPrimaryClip()` once. This
  is what catches everything copied while the keyboard was hidden — the common
  case — and it is the path that keeps working if listener callbacks are gated
  off.

Reading the clipboard happens in the service (it owns the `ClipboardManager`);
the extracted `text` + `copiedAt` are handed to the ViewModel, which applies the
§4.1 gates and dispatches the write to IO.

---

## 5. State and ViewModel

### 5.1 Panel state

`KeyboardState` currently carries a single `isEmojiShown: Boolean`. Two mutually
exclusive overlays make a boolean pair an invalid-state generator (both open at
once), so replace it with an enum:

```kotlin
enum class KeyboardPanel { NONE, EMOJI, CLIPBOARD }

data class KeyboardState(
    // …
    val panel: KeyboardPanel = KeyboardPanel.NONE,
) {
    val isEmojiShown: Boolean get() = panel == KeyboardPanel.EMOJI

    fun togglePanel(target: KeyboardPanel) =
        copy(panel = if (panel == target) KeyboardPanel.NONE else target)
}
```

Keeping `isEmojiShown` as a derived val means existing read sites keep compiling.
Two call sites need edits: `initialize()` (`copy(isEmojiShown = false)` →
`copy(panel = KeyboardPanel.NONE)`, since it's no longer settable) and
`toggleEmojiPopup()`, which `togglePanel` replaces.

### 5.2 ViewModel additions

```kotlin
var clips by mutableStateOf<List<ClipItem>>(emptyList())   // private set
val clipboardEnabled: Boolean get() = suggestionStripEnabled && !isSpecialLayout

fun toggleClipboard()                 // loads clips on open, like toggleEmoji reloads recentEmojis
fun onClipSelected(clip: ClipItem)
fun onClipPinToggle(clip: ClipItem)
fun onClipDelete(clip: ClipItem)
fun onClipboardChanged(text: String, copiedAt: Long)   // called by the service
```

`onClipSelected` mirrors `onSuggestionSelected`: `ic.commitText(clip.text, 1)`
(which replaces any selection automatically), play key-press feedback, reset
`lastCommittedChar`, then `updateShiftForCursor()` and `updateSuggestions()`.
Pasted text is deliberately **not** fed to `learnWord`/`learnBigram` — the user
didn't type it, and pasted content is exactly the kind of thing (addresses, IDs,
URLs) that would pollute the dictionary.

Tapping a clip closes the panel. Gboard leaves its panel open, but pasting is
almost always a one-shot action here and the closed panel puts the keys back
immediately. Easy to flip if it feels wrong in use.

---

## 6. UI

### 6.1 Suggestion strip — `compose/SuggestionStrip.kt`

Today: `Row { Row(weight = 1f) { suggestions }, emojiButton }`.
After: `Row { clipboardButton, Row(weight = 1f) { suggestions }, emojiButton }` —
one icon per side, suggestions centred between them.

The two buttons share one private `StripIconButton` composable rather than
duplicating the existing `Box`+`CircleShape`+`Icon` block. The `isEmojiShown`
parameter becomes `activePanel: KeyboardPanel` so each button can render an
active state and the suggestions can hide whenever any panel is open.

### 6.2 Clipboard panel — `compose/ClipboardLayout.kt`

Rendered as a sibling of `EmojiLayout` in the same overlay `Box` in `QqKeyboard`:

```kotlin
when (keyboardState.panel) {
    KeyboardPanel.EMOJI -> EmojiLayout(…)
    KeyboardPanel.CLIPBOARD -> ClipboardLayout(…)
    KeyboardPanel.NONE -> {}
}
```

It inherits `keyAreaHeight` from that `Box`, so it needs no height logic of its own.

**Structure**

- **Header row** — title plus a close ✕, reusing the circular close button
  styling from `EmojiLayout`'s `CategoryNavigationRow`.
- **Grid** — `LazyVerticalStaggeredGrid`, 2 columns. Staggered lets short clips
  stay short instead of every card padding out to the tallest one.
- **Card** — rounded `Box` on `colors.modifierBackground`, text at
  `maxLines = 4` with `TextOverflow.Ellipsis`, a pin badge in the corner when
  pinned, `combinedClickable` for tap-to-paste and long-press-to-select.
- **Empty state** — centred text, matching the `no_recent_emojis` treatment.

**Long-press actions.** `KeyButton` draws its long-press bubble inline with
`offset`/`shadow` rather than through a Compose `Popup`, and the panel should
follow suit — popups open their own window, which is a category of bug worth
avoiding inside an IME. So long-pressing a card highlights it and **swaps the
header for an action bar**: pin/unpin, delete, cancel. No new window, and it
reads clearly on a keyboard-sized surface.

### 6.3 New resources

Drawables (Material Symbols / Lucide, matching the existing mix):
`ic_clipboard.xml`, `ic_pin.xml`, `ic_pin_off.xml`, `ic_trash.xml`.
`ic_delete` is the backspace glyph and shouldn't be reused for "delete this clip".

`KeyboardDimensions` gains a `--- Clipboard ---` block: header height, column
count, card corner radius / padding / spacing, clip font size, max lines.

Strings are Karakalpak (`values/strings.xml` is the only values dir — there is no
translation split). Proposed keys — **wording needs your review**, these are a
starting point, not authoritative:

| Key | Draft |
| --- | --- |
| `clipboard_title` | Almasıw buferi |
| `clipboard_empty` | Kóshirilgen tekstler joq |
| `clipboard_hint` | Kóshirilgen tekst usı jerde 1 saat saqlanadı |
| `clip_pin` | Bekitiw |
| `clip_unpin` | Bekitiwdi biykarlaw |
| `clip_delete` | Óshiriw |
| `cd_clipboard` | Almasıw buferi |

---

## 7. Stages

Each stage is its own branch and PR, per the repo's git conventions.

### Stage 1 — `stage-1/clipboard-storage`

Data layer and capture, no UI. Nothing is user-visible yet; verification is via
`adb shell` inspection of the DB.

- `ClipItem`, `ClipboardDatabase`, `ClipboardRepository`
- `ClipboardManager` listener + `onStartInputView` poll in `QqKeyboardService`
- ViewModel capture entry point with the §4.1 gates
- Expiry and pruning
- **Device check:** confirm clips are captured on Android 12+ and note whether a
  clipboard-access toast appears

### Stage 2 — `stage-2/clipboard-panel`

The feature becomes usable: view and paste.

- `KeyboardPanel` enum refactor in `KeyboardState`
- Clipboard drawable + dimensions + strings
- `ClipboardLayout` with header, staggered grid, empty state
- Clipboard button at the start of the suggestion strip
- Overlay wiring in `QqKeyboard`, tap-to-paste

### Stage 3 — `stage-3/clipboard-pinning`

Management.

- Long-press → header swaps to the action bar
- Pin / unpin with pinned-section ordering, delete
- Pin badge on cards
- Pin/unpin/trash drawables

Stages 2 and 3 could be merged. Kept separate because Stage 2 alone is a coherent
increment (everything expires in an hour, nothing to manage), and a 2+3 PR would
be large enough to be awkward to review.

### Stage 4 — optional, `stage-4/clipboard-settings`

- "Clipboard history" toggle in settings, independent of the strip
- "Clear clipboard history" action
- Configurable retention (1 hour / 1 day / until deleted)

---

## 8. Deferred ideas

- **Paste chip in the strip.** Gboard surfaces the newest clip as a suggestion
  chip right after a copy. Cheap to add on top of Stage 2 and probably the
  highest-value follow-up.
- **Image clips.** Needs persistent copies of the images (URI grants die with the
  source app), a thumbnail grid, cache eviction, and `commitContent()` — plus
  many target apps reject image content anyway.
- **Manual clip entry** (Gboard's "+").
- **Fresh-clip indicator** on the strip's clipboard icon.

---

## 9. Risks

| Risk | Mitigation |
| --- | --- |
| Clipboard reads blocked or `null` on some OEM builds | Dual capture paths, `try/catch`, panel degrades to its empty state rather than breaking |
| Clipboard toast on every keyboard open (Android 12+) | Verify on device in Stage 1; if it appears, drop the poll and rely on the listener |
| Sensitive text (passwords, OTPs) landing in history | `EXTRA_IS_SENSITIVE` honoured, password fields excluded, 1-hour expiry, app-private storage |
| Panel long-press UI misbehaving inside the IME window | Inline action bar instead of `Popup`/`DropdownMenu`, matching `KeyButton`'s existing approach |
| DB work on the main thread stuttering key input | All repository calls `suspend` on `Dispatchers.IO`, same as `SuggestionRepository` |

---

## 10. Verification

The repo has no test sources today (`app/src/test` and `app/src/androidTest` do
not exist), so verification is manual per stage:

1. `./gradlew assembleDebug installDebug`
2. Copy text in another app → open a text field → the clip appears in the panel
3. Tap a clip → it is inserted at the cursor
4. Long-press → pin → wait past an hour (or backdate `copied_at` via `adb`) →
   pinned survives, unpinned is gone
5. Copy in a password field → nothing is captured
6. Turn the suggestion strip off → no clipboard button, no new clips recorded

If it's worth starting a test suite, the retention/pruning logic in
`ClipboardDatabase` is the natural first candidate — it's pure enough to test
against an in-memory SQLite database.
