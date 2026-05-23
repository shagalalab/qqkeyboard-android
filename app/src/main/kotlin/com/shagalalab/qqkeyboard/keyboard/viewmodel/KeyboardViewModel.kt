package com.shagalalab.qqkeyboard.keyboard.viewmodel

import android.content.Context
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shagalalab.qqkeyboard.keyboard.data.SuggestionRepository
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KeyboardViewModel : ViewModel() {

    private var preferences: KeyboardPreferences? = null
    private var feedbackManager: FeedbackManager? = null
    private var repository: SuggestionRepository? = null

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

    var suggestions by mutableStateOf<List<String>>(emptyList())
        private set

    private var currentWordForSuggestions by mutableStateOf("")

    val suggestionShiftState: ShiftState
        get() = shiftStateForWord(currentWordForSuggestions)

    private var inputConnection: InputConnection? = null
    private var editorInfo: EditorInfo? = null

    private var lastShiftTapTime: Long = 0L
    private var suggestionJob: Job? = null
    private var lastCommittedWord: String = ""

    companion object {
        private const val DOUBLE_TAP_DELAY_MS = 300L
        private const val SUGGESTION_DEBOUNCE_MS = 100L
        private val PUNCTUATION_AUTO_SPACE = setOf(",", ".", "?", "!", "…", ";", "—", "»", "”", ")")
        private val WORD_SPLIT_REGEX = Regex("""[\s.,!?;:()\[\]{}"'«»—–…]""")
    }

    fun initialize(context: Context) {
        if (preferences == null) {
            preferences = KeyboardPreferences(context)
            feedbackManager = FeedbackManager(context)
            recentEmojis = preferences?.recentEmojis ?: emptyList()
            viewModelScope.launch(Dispatchers.IO) {
                repository = SuggestionRepository(context.applicationContext)
            }
        }
        val lastLayout = preferences?.lastUsedLayout ?: KeyboardLayout.LATIN
        keyboardState = keyboardState.copy(layout = lastLayout, isEmojiShown = false)
        currentTheme = KeyboardThemes.getByName(preferences?.selectedTheme ?: "Light")
        topRowMode = preferences?.topRowMode ?: TopRowMode.EXTRA_LETTERS
        keyboardHeight = preferences?.keyboardHeight ?: KeyboardHeight.DEFAULT
        keyBorderEnabled = preferences?.keyBorderEnabled ?: true
    }

    fun setInputConnection(connection: InputConnection?) {
        inputConnection = connection
        if (connection == null) {
            suggestions = emptyList()
            lastCommittedWord = ""
        }
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
        if (!isSuggestionsAllowed()) suggestions = emptyList()
    }

    fun onKeyPressed(key: String) {
        inputConnection?.let { ic ->
            when (key) {
                "BACKSPACE" -> {
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
                    feedbackManager?.playBackspaceFeedback()
                    updateShiftForCursor()
                    updateSuggestions()
                }
                "ENTER" -> {
                    learnCurrentWord()
                    editorInfo?.let { info ->
                        val imeAction = info.imeOptions and (EditorInfo.IME_MASK_ACTION or EditorInfo.IME_FLAG_NO_ENTER_ACTION)
                        when (imeAction) {
                            EditorInfo.IME_ACTION_GO,
                            EditorInfo.IME_ACTION_SEARCH,
                            EditorInfo.IME_ACTION_SEND,
                            EditorInfo.IME_ACTION_NEXT,
                            EditorInfo.IME_ACTION_PREVIOUS,
                            EditorInfo.IME_ACTION_DONE -> {
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
                    lastCommittedWord = ""
                    suggestions = emptyList()
                }
                "SPACE" -> {
                    val committedWord = getCurrentWord()
                    learnCurrentWord()
                    val textBefore = ic.getTextBeforeCursor(1, 0)?.toString()
                    if ((preferences?.doubleSpacePeriodEnabled ?: true) && textBefore == " ") {
                        val contextBefore = ic.getTextBeforeCursor(30, 0)?.toString() ?: ""

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
                    if (committedWord.isNotEmpty()) {
                        lastCommittedWord = committedWord
                        updateSuggestions()
                    } else {
                        suggestions = emptyList()
                    }
                }
                "SHIFT" -> {
                    val currentTime = System.currentTimeMillis()
                    val timeSinceLast = currentTime - lastShiftTapTime

                    if (timeSinceLast <= DOUBLE_TAP_DELAY_MS && lastShiftTapTime != 0L) {
                        keyboardState = keyboardState.doubleTapShift()
                        lastShiftTapTime = 0L
                    } else {
                        toggleShift()
                        lastShiftTapTime = currentTime
                    }
                    feedbackManager?.playKeyPressFeedback()
                }
                "LAYOUT_SWITCH" -> {
                    switchLanguage()
                    feedbackManager?.playKeyPressFeedback()
                    updateSuggestions()
                }
                "123" -> {
                    switchToLayout(KeyboardLayout.NUMERIC)
                    feedbackManager?.playKeyPressFeedback()
                    suggestions = emptyList()
                }
                "ABC" -> {
                    switchToLastLanguage()
                    feedbackManager?.playKeyPressFeedback()
                    updateSuggestions()
                }
                "EMOJI" -> {
                    toggleEmoji()
                    feedbackManager?.playKeyPressFeedback()
                }
                "€~\\" -> {
                    switchToLayout(KeyboardLayout.SYMBOLIC)
                    feedbackManager?.playKeyPressFeedback()
                    suggestions = emptyList()
                }
                else -> {
                    val textToCommit = if (keyboardState.shouldShowUpperCase) {
                        key.kaaUppercase()
                    } else {
                        key.lowercase()
                    }
                    ic.commitText(textToCommit, 1)
                    if ((preferences?.autoSpaceAfterPunctuation ?: false) && textToCommit in PUNCTUATION_AUTO_SPACE) {
                        ic.commitText(" ", 1)
                    }
                    feedbackManager?.playKeyPressFeedback()

                    if (isEmoji(key)) {
                        addRecentEmoji(key)
                    }

                    keyboardState = keyboardState.resetShift()
                    updateShiftForCursor()
                    updateSuggestions()
                }
            }
        }
    }

    fun onSuggestionSelected(suggestion: String) {
        inputConnection?.let { ic ->
            val currentWord = getCurrentWord()
            if (currentWord.isNotEmpty()) {
                ic.deleteSurroundingText(currentWord.length, 0)
            }
            ic.commitText("$suggestion ", 1)
            feedbackManager?.playKeyPressFeedback()

            val suggestionLower = suggestion.lowercase()
            val prev = lastCommittedWord
            val script = currentScript()
            if (prev.isNotEmpty() && script != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    repository?.learnWord(suggestionLower, script)
                    repository?.learnBigram(prev, suggestionLower, script)
                }
            }

            lastCommittedWord = suggestionLower
            updateShiftForCursor()
            updateSuggestions()
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
    }

    fun toggleEmoji() {
        keyboardState = keyboardState.toggleEmojiPopup()
    }

    private fun isSuggestionsAllowed(): Boolean {
        val info = editorInfo ?: return true
        val variation = info.inputType and InputType.TYPE_MASK_VARIATION
        if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) return false
        if (info.imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING != 0) return false
        return true
    }

    private fun shiftStateForWord(word: String): ShiftState {
        val letters = word.filter { it.isLetter() }
        return when {
            letters.isEmpty() -> keyboardState.shiftState
            letters.length > 1 && letters.all { it.isUpperCase() } -> ShiftState.CAPS_LOCK
            letters.first().isUpperCase() -> ShiftState.ON
            else -> ShiftState.OFF
        }
    }

    private fun getCurrentWord(): String {
        val text = inputConnection?.getTextBeforeCursor(100, 0)?.toString() ?: return ""
        return text.split(WORD_SPLIT_REGEX).lastOrNull() ?: ""
    }

    private fun currentScript(): String? = when (keyboardState.layout) {
        KeyboardLayout.LATIN -> "latin"
        KeyboardLayout.CYRILLIC -> "cyrillic"
        else -> null
    }

    private fun updateSuggestions() {
        if (!isSuggestionsAllowed()) { suggestions = emptyList(); return }
        val script = currentScript() ?: run { suggestions = emptyList(); return }
        val word = getCurrentWord()
        currentWordForSuggestions = word

        suggestionJob?.cancel()
        suggestionJob = viewModelScope.launch {
            val results = if (word.isEmpty()) {
                // No prefix being typed — show bigram predictions for last committed word
                withContext(Dispatchers.IO) {
                    repository?.getBigramPredictions(lastCommittedWord, script) ?: emptyList()
                }
            } else {
                delay(SUGGESTION_DEBOUNCE_MS)
                withContext(Dispatchers.IO) {
                    repository?.getSuggestions(word, script) ?: emptyList()
                }
            }
            suggestions = results
        }
    }

    private fun learnCurrentWord() {
        val script = currentScript() ?: return
        val word = getCurrentWord()
        if (word.length < 3) return
        if (!isSuggestionsAllowed()) return

        val prev = lastCommittedWord  // capture on main thread before launching coroutine
        viewModelScope.launch(Dispatchers.IO) {
            repository?.learnWord(word, script)
            if (prev.isNotEmpty()) {
                repository?.learnBigram(prev, word, script)
            }
        }
    }

    private fun toggleShift() {
        keyboardState = keyboardState.toggleShift()
    }

    private fun switchLanguage() {
        when (keyboardState.layout) {
            KeyboardLayout.LATIN, KeyboardLayout.CYRILLIC -> {
                keyboardState = keyboardState.switchLanguage()
                preferences?.lastUsedLayout = keyboardState.layout
            }
            KeyboardLayout.NUMERIC, KeyboardLayout.SYMBOLIC -> {
                val currentLanguage = preferences?.lastUsedLayout ?: KeyboardLayout.LATIN
                val newLanguage = when (currentLanguage) {
                    KeyboardLayout.LATIN -> KeyboardLayout.CYRILLIC
                    KeyboardLayout.CYRILLIC -> KeyboardLayout.LATIN
                    else -> KeyboardLayout.CYRILLIC
                }
                preferences?.lastUsedLayout = newLanguage
            }
            KeyboardLayout.NUMBER_PAD, KeyboardLayout.NUMBER_PASSWORD, KeyboardLayout.PHONE -> {
            }
        }
    }

    private fun switchToLayout(layout: KeyboardLayout) {
        keyboardState = keyboardState.switchToLayout(layout)
        if (layout == KeyboardLayout.LATIN || layout == KeyboardLayout.CYRILLIC) {
            preferences?.lastUsedLayout = layout
        }
    }

    private fun switchToLastLanguage() {
        val lastLanguageLayout = preferences?.lastUsedLayout ?: KeyboardLayout.LATIN
        keyboardState = keyboardState.switchToLayout(lastLanguageLayout)
    }

    private fun addRecentEmoji(emoji: String) {
        preferences?.addRecentEmoji(emoji)
        recentEmojis = preferences?.recentEmojis ?: emptyList()
    }

    fun getLayoutSwitchButtonText(): String {
        return when (keyboardState.layout) {
            KeyboardLayout.LATIN -> "ҚҚ"
            KeyboardLayout.CYRILLIC -> "QQ"
            else -> {
                val lastLanguage = preferences?.lastUsedLayout ?: KeyboardLayout.LATIN
                when (lastLanguage) {
                    KeyboardLayout.LATIN -> "ҚҚ"
                    else -> "QQ"
                }
            }
        }
    }

    private fun updateShiftForCursor() {
        if (keyboardState.shiftState == ShiftState.CAPS_LOCK) return
        if (!(preferences?.autoCapEnabled ?: true)) {
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

        val codePoint = text.codePointAt(0)

        return when (codePoint) {
            in 0x1F600..0x1F64F -> true
            in 0x1F300..0x1F5FF -> true
            in 0x1F680..0x1F6FF -> true
            in 0x2600..0x26FF -> true
            in 0x2700..0x27BF -> true
            in 0xFE00..0xFE0F -> true
            in 0x1F900..0x1F9FF -> true
            in 0x1F1E6..0x1F1FF -> true
            in 0x1FA70..0x1FAFF -> true
            else -> {
                text.length > 1 && text.any { char ->
                    val cp = char.code
                    cp in 0x1F600..0x1F64F || cp in 0x1F300..0x1F5FF ||
                        cp in 0x1F680..0x1F6FF || cp in 0x2600..0x26FF
                }
            }
        }
    }
}
