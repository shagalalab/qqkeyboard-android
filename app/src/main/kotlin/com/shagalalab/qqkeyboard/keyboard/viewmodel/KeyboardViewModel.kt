package com.shagalalab.qqkeyboard.keyboard.viewmodel

import android.content.Context
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.shagalalab.qqkeyboard.keyboard.feedback.FeedbackManager
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardLayout
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardState
import com.shagalalab.qqkeyboard.keyboard.model.ShiftState
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardTheme
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardThemes
import com.shagalalab.qqkeyboard.keyboard.utils.EmojiUtils
import com.shagalalab.qqkeyboard.keyboard.utils.kaaUppercase

class KeyboardViewModel : ViewModel() {

    private var preferences: KeyboardPreferences? = null
    private var feedbackManager: FeedbackManager? = null

    var keyboardState by mutableStateOf(KeyboardState())
        private set

    var recentEmojis by mutableStateOf<List<String>>(emptyList())
        private set

    var currentImeAction by mutableStateOf<Int?>(null)
        private set

    var currentTheme by mutableStateOf<KeyboardTheme>(KeyboardThemes.Light)
        private set

    var topRowMode by mutableStateOf(TopRowMode.EXTRA_LETTERS)
        private set

    var keyboardHeight by mutableStateOf(KeyboardHeight.DEFAULT)
        private set

    var keyBorderEnabled by mutableStateOf(true)
        private set

    private var autoCapEnabled = true
    private var doubleSpacePeriodEnabled = true

    private var inputConnection: InputConnection? = null
    private var editorInfo: EditorInfo? = null

    private var lastShiftTapTime: Long = 0L

    companion object {
        private const val DOUBLE_TAP_DELAY_MS = 300L
    }

    fun initialize(context: Context) {
        if (preferences == null) {
            preferences = KeyboardPreferences(context)
            feedbackManager = FeedbackManager(context)
            // Initialize recent emojis state
            recentEmojis = preferences?.recentEmojis ?: emptyList()
        }
        // Reload per-session preferences (re-evaluated every time keyboard is shown)
        val lastLayout = preferences?.lastUsedLayout ?: KeyboardLayout.LATIN
        keyboardState = keyboardState.copy(layout = lastLayout, isEmojiShown = false)
        currentTheme = KeyboardThemes.getByName(preferences?.selectedTheme ?: "Light")
        topRowMode = preferences?.topRowMode ?: TopRowMode.EXTRA_LETTERS
        keyboardHeight = preferences?.keyboardHeight ?: KeyboardHeight.DEFAULT
        keyBorderEnabled = preferences?.keyBorderEnabled ?: true
        autoCapEnabled = preferences?.autoCapEnabled ?: true
        doubleSpacePeriodEnabled = preferences?.doubleSpacePeriodEnabled ?: true
    }

    fun setInputConnection(connection: InputConnection?) {
        inputConnection = connection
    }

    fun setEditorInfo(info: EditorInfo?) {
        editorInfo = info
        val newImeAction = info?.let { it.imeOptions and (EditorInfo.IME_MASK_ACTION or EditorInfo.IME_FLAG_NO_ENTER_ACTION) }
        currentImeAction = newImeAction

        if (info != null) {
            val inputClass = info.inputType and InputType.TYPE_MASK_CLASS
            val inputVariation = info.inputType and InputType.TYPE_MASK_VARIATION
            val specialLayout = when (inputClass) {
                InputType.TYPE_CLASS_PHONE -> KeyboardLayout.PHONE
                InputType.TYPE_CLASS_NUMBER -> when (inputVariation) {
                    InputType.TYPE_NUMBER_VARIATION_PASSWORD -> KeyboardLayout.NUMBER_PASSWORD
                    else -> KeyboardLayout.NUMBER_PAD
                }
                else -> null
            }
            if (specialLayout != null) {
                keyboardState = keyboardState.switchToLayout(specialLayout)
            } else if (keyboardState.layout in setOf(
                    KeyboardLayout.NUMBER_PAD,
                    KeyboardLayout.NUMBER_PASSWORD,
                    KeyboardLayout.PHONE
                )
            ) {
                val lastLayout = preferences?.lastUsedLayout ?: KeyboardLayout.LATIN
                keyboardState = keyboardState.switchToLayout(lastLayout)
            }
        }
        updateShiftForCursor()
    }

    fun onKeyPressed(key: String) {
        inputConnection?.let { ic ->
            when (key) {
                "BACKSPACE" -> {
                    val selectedText = ic.getSelectedText(0)
                    if (selectedText != null && selectedText.isNotEmpty()) {
                        // Delete selected text by replacing with empty string
                        ic.commitText("", 1)
                    } else {
                        // Smart deletion - handles emojis and regular characters
                        val textBefore = ic.getTextBeforeCursor(20, 0)?.toString()
                        val deleteLength = EmojiUtils.getGraphemeClusterLength(textBefore)
                        if (deleteLength > 0) {
                            ic.deleteSurroundingText(deleteLength, 0)
                        }
                    }
                    feedbackManager?.playBackspaceFeedback()
                    updateShiftForCursor()
                }
                "ENTER" -> {
                    editorInfo?.let { info ->
                        val imeAction = info.imeOptions and (EditorInfo.IME_MASK_ACTION or EditorInfo.IME_FLAG_NO_ENTER_ACTION)
                        when (imeAction) {
                            EditorInfo.IME_ACTION_GO,
                            EditorInfo.IME_ACTION_SEARCH,
                            EditorInfo.IME_ACTION_SEND,
                            EditorInfo.IME_ACTION_NEXT,
                            EditorInfo.IME_ACTION_PREVIOUS,
                            EditorInfo.IME_ACTION_DONE -> {
                                // Perform the IME action instead of inserting newline
                                ic.performEditorAction(imeAction)
                            }
                            else -> {
                                ic.commitText("\n", 1)
                                updateShiftForCursor()
                            }
                        }
                    } ?: run {
                        ic.commitText("\n", 1)
                        updateShiftForCursor()
                    }
                    feedbackManager?.playReturnFeedback()
                }
                "SPACE" -> {
                    // Check for double-space to period conversion
                    val textBefore = ic.getTextBeforeCursor(1, 0)?.toString()
                    if (doubleSpacePeriodEnabled && textBefore == " ") {
                        // Previous character is space - check if we should convert to period
                        // Use larger context window for regex-based detection
                        val contextBefore = ic.getTextBeforeCursor(30, 0)?.toString() ?: ""

                        // Use regex to check if there's already a period followed by any number of spaces at the end
                        // Pattern: period followed by one or more spaces at end of string
                        val periodSpacePattern = Regex("""\.\s+$""")
                        val exclamationSpacePattern = Regex("""!\s+$""")
                        val questionSpacePattern = Regex("""\?\s+$""")

                        when {
                            periodSpacePattern.containsMatchIn(contextBefore) ||
                            exclamationSpacePattern.containsMatchIn(contextBefore) ||
                            questionSpacePattern.containsMatchIn(contextBefore) -> {
                                ic.commitText(" ", 1)
                            }
                            else -> {
                                ic.deleteSurroundingText(1, 0)
                                ic.commitText(". ", 1)
                            }
                        }
                        updateShiftForCursor()
                    } else {
                        ic.commitText(" ", 1)
                        updateShiftForCursor()
                    }
                    feedbackManager?.playSpacebarFeedback()
                }
                "SHIFT" -> {
                    val currentTime = System.currentTimeMillis()
                    val timeSinceLast = currentTime - lastShiftTapTime

                    if (timeSinceLast <= DOUBLE_TAP_DELAY_MS && lastShiftTapTime != 0L) {
                        // Double tap detected - activate caps lock
                        keyboardState = keyboardState.doubleTapShift()
                        lastShiftTapTime = 0L // Reset to prevent triple-tap issues
                    } else {
                        // Single tap - normal toggle
                        toggleShift()
                        lastShiftTapTime = currentTime
                    }
                    feedbackManager?.playKeyPressFeedback()
                }
                "LAYOUT_SWITCH" -> {
                    switchLanguage()
                    feedbackManager?.playKeyPressFeedback()
                }
                "123" -> {
                    switchToLayout(KeyboardLayout.NUMERIC)
                    feedbackManager?.playKeyPressFeedback()
                }
                "ABC" -> {
                    switchToLastLanguage()
                    feedbackManager?.playKeyPressFeedback()
                }
                "EMOJI" -> {
                    toggleEmoji()
                    feedbackManager?.playKeyPressFeedback()
                }
                "€~\\" -> {
                    switchToLayout(KeyboardLayout.SYMBOLIC)
                    feedbackManager?.playKeyPressFeedback()
                }
                else -> {
                    val textToCommit = if (keyboardState.shouldShowUpperCase) {
                        key.kaaUppercase()
                    } else {
                        key.lowercase()
                    }
                    ic.commitText(textToCommit, 1)
                    feedbackManager?.playKeyPressFeedback()

                    // Track emoji usage for recent emojis
                    if (isEmoji(key)) {
                        addRecentEmoji(key)
                    }

                    keyboardState = keyboardState.resetShift()
                    updateShiftForCursor()
                }
            }
        }
    }

    fun onBackspaceRepeat() {
        inputConnection?.let { ic ->
            val selectedText = ic.getSelectedText(0)
            if (selectedText != null && selectedText.isNotEmpty()) {
                ic.commitText("", 1)
            } else {
                val textBefore = ic.getTextBeforeCursor(20, 0)?.toString()
                val deleteLength = EmojiUtils.getGraphemeClusterLength(textBefore)
                if (deleteLength > 0) {
                    ic.deleteSurroundingText(deleteLength, 0)
                }
            }
        }
    }

    fun onShiftLongPress() {
        keyboardState = keyboardState.doubleTapShift()
    }

    fun onBackspaceLongPress() {
        // This method is called by QqKeyboard.kt but the actual repetitive
        // deletion is handled by KeyButton.kt's LaunchedEffect mechanism
        // We can use this for any setup if needed in the future
    }

    fun toggleEmoji() {
        keyboardState = keyboardState.toggleEmojiPopup()
    }

    private fun toggleShift() {
        keyboardState = keyboardState.toggleShift()
    }

    private fun switchLanguage() {
        when (keyboardState.layout) {
            KeyboardLayout.LATIN, KeyboardLayout.CYRILLIC -> {
                // Direct language switching for alphabetic layouts
                keyboardState = keyboardState.switchLanguage()
                preferences?.lastUsedLayout = keyboardState.layout
            }
            KeyboardLayout.NUMERIC, KeyboardLayout.SYMBOLIC -> {
                // For numeric/symbolic modes, switch the underlying language preference
                val currentLanguage = preferences?.lastUsedLayout ?: KeyboardLayout.LATIN
                val newLanguage = when (currentLanguage) {
                    KeyboardLayout.LATIN -> KeyboardLayout.CYRILLIC
                    KeyboardLayout.CYRILLIC -> KeyboardLayout.LATIN
                    else -> KeyboardLayout.CYRILLIC
                }
                preferences?.lastUsedLayout = newLanguage
                // Stay in current input mode, just update the preference
            }
            KeyboardLayout.NUMBER_PAD, KeyboardLayout.NUMBER_PASSWORD, KeyboardLayout.PHONE -> {
                // No language switch in specialized input layouts
            }
        }
    }

    private fun switchToLayout(layout: KeyboardLayout) {
        keyboardState = keyboardState.switchToLayout(layout)
        // Save preference only for language layouts
        if (layout == KeyboardLayout.LATIN || layout == KeyboardLayout.CYRILLIC) {
            preferences?.lastUsedLayout = layout
        }
    }

    private fun switchToLastLanguage() {
        val lastLanguageLayout = preferences?.lastUsedLayout ?: KeyboardLayout.LATIN
        keyboardState = keyboardState.switchToLayout(lastLanguageLayout)
    }

    private fun addRecentEmoji(emoji: String) {
        // Update SharedPreferences
        preferences?.addRecentEmoji(emoji)
        // Update Compose state to trigger recomposition
        recentEmojis = preferences?.recentEmojis ?: emptyList()
    }

    fun getLayoutSwitchButtonText(): String {
        return when (keyboardState.layout) {
            KeyboardLayout.LATIN -> "ҚҚ"
            KeyboardLayout.CYRILLIC -> "QQ"
            // For numeric/symbolic modes, show switch based on last language
            else -> {
                val lastLanguage = preferences?.lastUsedLayout ?: KeyboardLayout.LATIN
                when (lastLanguage) {
                    KeyboardLayout.LATIN -> "ҚҚ"
                    else -> "QQ"
                }
            }
        }
    }

    // Uses Android's built-in getCursorCapsMode which reads the editor's autocap flags
    // (CAP_SENTENCES, CAP_WORDS, CAP_CHARACTERS) and the actual cursor context together,
    // correctly handling empty fields, after newline, and after sentence-ending punctuation.
    private fun updateShiftForCursor() {
        if (keyboardState.shiftState == ShiftState.CAPS_LOCK) return
        if (!autoCapEnabled) {
            keyboardState = keyboardState.resetShift()
            return
        }
        val capsMode = inputConnection?.getCursorCapsMode(editorInfo?.inputType ?: 0) ?: 0
        keyboardState = if (capsMode != 0) {
            keyboardState.enableAutoCapitalization()
        } else {
            keyboardState.resetShift()
        }
    }

    private fun isEmoji(text: String): Boolean {
        if (text.isEmpty()) return false

        // Use the EmojiUtils to check if this is an emoji
        // We'll consider it an emoji if it's not a simple ASCII character
        // and contains characters in the emoji Unicode ranges
        val codePoint = text.codePointAt(0)

        return when (codePoint) {
            in 0x1F600..0x1F64F -> true // Emoticons
            in 0x1F300..0x1F5FF -> true // Miscellaneous Symbols
            in 0x1F680..0x1F6FF -> true // Transport and Map Symbols
            in 0x2600..0x26FF -> true   // Miscellaneous Symbols
            in 0x2700..0x27BF -> true   // Dingbats
            in 0xFE00..0xFE0F -> true   // Variation Selectors
            in 0x1F900..0x1F9FF -> true // Supplemental Symbols
            in 0x1F1E6..0x1F1FF -> true // Regional Indicators (flags)
            // Additional ranges for newer emojis
            in 0x1FA70..0x1FAFF -> true // Symbols and Pictographs Extended-A
            else -> {
                // Check if it's a multi-codepoint emoji by checking length vs display length
                text.length > 1 && text.any { char ->
                    val cp = char.code
                    cp in 0x1F600..0x1F64F || cp in 0x1F300..0x1F5FF ||
                        cp in 0x1F680..0x1F6FF || cp in 0x2600..0x26FF
                }
            }
        }
    }
}
