package com.shagalalab.qqkeyboard.keyboard.viewmodel

import android.content.Context
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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
    
    fun initialize(context: Context) {
        if (preferences == null) {
            preferences = KeyboardPreferences(context)
            feedbackManager = FeedbackManager(context)
            // Load last used layout
            val lastLayout = preferences?.lastUsedLayout ?: LayoutType.LATIN
            keyboardState = keyboardState.copy(layoutType = lastLayout)
        }
    }
    
    fun setInputConnection(connection: InputConnection?) {
        inputConnection = connection
    }
    
    fun onKeyPressed(key: String) {
        inputConnection?.let { ic ->
            when {
                key == "BACKSPACE" -> {
                    ic.deleteSurroundingText(1, 0)
                    feedbackManager?.playBackspaceFeedback()
                }
                key == "ENTER" -> {
                    ic.commitText("\n", 1)
                    feedbackManager?.playReturnFeedback()
                }
                key == "SPACE" -> {
                    ic.commitText(" ", 1)
                    feedbackManager?.playSpacebarFeedback()
                }
                key == "SHIFT" -> {
                    toggleShift()
                    feedbackManager?.playKeyPressFeedback()
                }
                key == "LAYOUT_SWITCH" -> {
                    switchLayout()
                    feedbackManager?.playKeyPressFeedback()
                }
                key == "123" -> {
                    switchToNumeric()
                    feedbackManager?.playKeyPressFeedback()
                }
                key == "ABC" -> {
                    switchToAlphabetic()
                    feedbackManager?.playKeyPressFeedback()
                }
                key == "EMOJI" -> {
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
}