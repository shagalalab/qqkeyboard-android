package com.shagalalab.qqkeyboard.keyboard.viewmodel

import android.content.Context
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.feedback.FeedbackManager
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardMode
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardState
import com.shagalalab.qqkeyboard.keyboard.model.LayoutType
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences
import com.shagalalab.qqkeyboard.keyboard.utils.EmojiUtils

class KeyboardViewModel : ViewModel() {

    private var preferences: KeyboardPreferences? = null
    private var feedbackManager: FeedbackManager? = null

    var keyboardState by mutableStateOf(KeyboardState())
        private set

    var recentEmojis by mutableStateOf<List<String>>(emptyList())
        private set

    private var inputConnection: InputConnection? = null

    private var wordSeparators: Set<Char> = setOf()
    private var lastShiftTapTime: Long = 0L

    companion object {
        private const val DOUBLE_TAP_DELAY_MS = 300L
    }

    fun initialize(context: Context) {
        if (preferences == null) {
            preferences = KeyboardPreferences(context)
            feedbackManager = FeedbackManager(context)
            // Load last used layout
            val lastLayout = preferences?.lastUsedLayout ?: LayoutType.LATIN
            keyboardState = keyboardState.copy(layoutType = lastLayout)
            // Initialize recent emojis state
            recentEmojis = preferences?.recentEmojis ?: emptyList()
            wordSeparators = context.resources.getString(R.string.word_separators).toSet()
        }
    }

    fun setInputConnection(connection: InputConnection?) {
        inputConnection = connection
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
                }
                "ENTER" -> {
                    ic.commitText("\n", 1)
                    feedbackManager?.playReturnFeedback()
                }
                "SPACE" -> {
                    // Check for double-space to period conversion
                    val textBefore = ic.getTextBeforeCursor(1, 0)?.toString()
                    if (textBefore == " ") {
                        // Previous character is space - check context before the space
                        val contextBefore = ic.getTextBeforeCursor(3, 0)?.toString() ?: ""

                        // Check if there's already sentence-ending punctuation before the space
                        if (contextBefore.length >= 2) {
                            val charBeforeSpace = contextBefore[contextBefore.length - 2]
                            if (charBeforeSpace == '.' || charBeforeSpace == '!' || charBeforeSpace == '?') {
                                // Already has punctuation, just add another space
                                ic.commitText(" ", 1)
                            } else {
                                // No punctuation, convert "  " to ". "
                                ic.deleteSurroundingText(1, 0) // Remove the previous space
                                ic.commitText(". ", 1) // Add period and space

                                // Auto-capitalize after period
                                keyboardState = keyboardState.enableAutoCapitalization()
                            }
                        } else {
                            // Not enough context, assume no punctuation - convert to period
                            ic.deleteSurroundingText(1, 0) // Remove the previous space
                            ic.commitText(". ", 1) // Add period and space

                            // Auto-capitalize after period
                            keyboardState = keyboardState.enableAutoCapitalization()
                        }
                    } else {
                        // Normal space insertion
                        ic.commitText(" ", 1)

                        // Check if we should auto-capitalize after this space
                        if (shouldAutoCapitalize()) {
                            keyboardState = keyboardState.enableAutoCapitalization()
                        }
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
                    switchLayout()
                    feedbackManager?.playKeyPressFeedback()
                }
                "123" -> {
                    switchToNumeric()
                    feedbackManager?.playKeyPressFeedback()
                }
                "ABC" -> {
                    switchToAlphabetic()
                    feedbackManager?.playKeyPressFeedback()
                }
                "EMOJI" -> {
                    toggleEmoji()
                    feedbackManager?.playKeyPressFeedback()
                }
                "€~\\" -> {
                    switchToSymbolic()
                    feedbackManager?.playKeyPressFeedback()
                }
                else -> {
                    val textToCommit = if (keyboardState.shouldShowUpperCase) {
                        key.uppercase()
                    } else {
                        key.lowercase()
                    }
                    ic.commitText(textToCommit, 1)
                    feedbackManager?.playKeyPressFeedback()

                    // Track emoji usage for recent emojis
                    if (isEmoji(key)) {
                        addRecentEmoji(key)
                    }

                    // Check if we should auto-capitalize after this input
                    keyboardState = if (shouldAutoCapitalizeAfterInput(textToCommit)) {
                        keyboardState.enableAutoCapitalization()
                    } else {
                        // Reset shift state after typing (except for caps lock)
                        keyboardState.resetShift()
                    }
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

    private fun switchLayout() {
        keyboardState = keyboardState.switchLayout()
        // Save the new layout preference
        preferences?.lastUsedLayout = keyboardState.layoutType
    }

    private fun switchToNumeric() {
        keyboardState = keyboardState.switchMode(KeyboardMode.NUMERIC)
    }

    private fun switchToAlphabetic() {
        keyboardState = keyboardState.switchMode(KeyboardMode.ALPHABETIC)
    }

    private fun switchToSymbolic() {
        keyboardState = keyboardState.switchMode(KeyboardMode.SYMBOLIC)
    }

    private fun addRecentEmoji(emoji: String) {
        // Update SharedPreferences
        preferences?.addRecentEmoji(emoji)
        // Update Compose state to trigger recomposition
        recentEmojis = preferences?.recentEmojis ?: emptyList()
    }

    fun getLayoutSwitchButtonText(): String {
        return when (keyboardState.layoutType) {
            LayoutType.LATIN -> "ҚҚ"
            LayoutType.CYRILLIC -> "QQ"
        }
    }

    private fun shouldAutoCapitalize(): Boolean {
        inputConnection?.let { ic ->
            // Get text before cursor (up to 10 characters should be enough)
            val textBefore = ic.getTextBeforeCursor(10, 0)?.toString() ?: return false

            // Check if text ends with sentence ending punctuation followed by space
            if (textBefore.length >= 2) {
                val lastChar = textBefore[textBefore.length - 1]
                val secondLastChar = textBefore[textBefore.length - 2]

                // Check for pattern: punctuation + space
                if (lastChar == ' ' && wordSeparators.contains(secondLastChar)) {
                    return true
                }
            }

            // Also check if we're at the beginning of input (should capitalize first letter)
            return textBefore.isEmpty() || textBefore.trim().isEmpty()
        }
        return false
    }

    private fun shouldAutoCapitalizeAfterInput(inputText: String): Boolean {
        // Check if the input text ends with sentence-ending punctuation followed by space
        return inputText.length >= 2 &&
               inputText.endsWith(" ") &&
               (inputText.endsWith(". ") || inputText.endsWith("! ") || inputText.endsWith("? "))
    }

    private fun isEmoji(text: String): Boolean {
        if (text.isEmpty()) return false

        // Use the EmojiUtils to check if this is an emoji
        // We'll consider it an emoji if it's not a simple ASCII character
        // and contains characters in the emoji Unicode ranges
        val codePoint = text.codePointAt(0)

        return when {
            // Basic emoticons and symbols
            codePoint in 0x1F600..0x1F64F -> true // Emoticons
            codePoint in 0x1F300..0x1F5FF -> true // Miscellaneous Symbols
            codePoint in 0x1F680..0x1F6FF -> true // Transport and Map Symbols
            codePoint in 0x2600..0x26FF -> true   // Miscellaneous Symbols
            codePoint in 0x2700..0x27BF -> true   // Dingbats
            codePoint in 0xFE00..0xFE0F -> true   // Variation Selectors
            codePoint in 0x1F900..0x1F9FF -> true // Supplemental Symbols
            codePoint in 0x1F1E6..0x1F1FF -> true // Regional Indicators (flags)
            // Additional ranges for newer emojis
            codePoint in 0x1FA70..0x1FAFF -> true // Symbols and Pictographs Extended-A
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
