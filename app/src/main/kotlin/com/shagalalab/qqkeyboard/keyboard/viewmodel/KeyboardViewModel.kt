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

class KeyboardViewModel : ViewModel() {

    private var preferences: KeyboardPreferences? = null
    private var feedbackManager: FeedbackManager? = null

    var keyboardState by mutableStateOf(KeyboardState())
        private set

    private var inputConnection: InputConnection? = null

    private var wordSeparators: Set<Char> = setOf()

    fun initialize(context: Context) {
        if (preferences == null) {
            preferences = KeyboardPreferences(context)
            feedbackManager = FeedbackManager(context)
            // Load last used layout
            val lastLayout = preferences?.lastUsedLayout ?: LayoutType.LATIN
            keyboardState = keyboardState.copy(layoutType = lastLayout)
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
                        // No selection, delete one character before cursor
                        ic.deleteSurroundingText(1, 0)
                    }
                    feedbackManager?.playBackspaceFeedback()
                }
                "ENTER" -> {
                    ic.commitText("\n", 1)
                    feedbackManager?.playReturnFeedback()
                }
                "SPACE" -> {
                    ic.commitText(" ", 1)
                    feedbackManager?.playSpacebarFeedback()

                    // Check if we should auto-capitalize after this space
                    if (shouldAutoCapitalize()) {
                        keyboardState = keyboardState.enableAutoCapitalization()
                    }
                }
                "SHIFT" -> {
                    toggleShift()
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
                    switchToEmoji()
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

                    // Reset shift state after typing (except for caps lock)
                    keyboardState = keyboardState.resetShift()
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

    private fun switchToEmoji() {
        keyboardState = keyboardState.switchMode(KeyboardMode.EMOJI)
    }

    fun getLayoutSwitchButtonText(): String {
        return when (keyboardState.layoutType) {
            LayoutType.LATIN -> "ҚҚ"
            LayoutType.CYRILLIC -> "QQ"
        }
    }

    fun getModeButtonText(): String {
        return when (keyboardState.keyboardMode) {
            KeyboardMode.ALPHABETIC -> "123"
            KeyboardMode.NUMERIC -> "АБВ"
            KeyboardMode.EMOJI -> "АБВ"
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
}
