package com.shagalalab.qqkeyboard.keyboard.model

import android.view.inputmethod.EditorInfo
import com.shagalalab.qqkeyboard.R

enum class KeyType {
    CHARACTER,
    MODIFIER,
    ACTION,
    SPACE,
    LAYOUT_SWITCH
}

data class KeyData(
    val code: String,
    val displayText: String,
    val keyType: KeyType = KeyType.CHARACTER,
    val iconResId: Int? = null,
    val widthRatio: Float = 1f,
    val fillRight: Boolean = false,
    val longPressCode: String? = null,
    val alternativeChars: List<String> = emptyList(),
    val hintText: String? = null,
) {
    companion object {
        // Character keys
        fun character(code: String, displayText: String = code) = KeyData(
            code = code,
            displayText = displayText,
            keyType = KeyType.CHARACTER
        )

        // Modifier keys
        fun shift(widthRatio: Float = 1.5f) = KeyData(
            code = "SHIFT",
            displayText = "",
            keyType = KeyType.MODIFIER,
            iconResId = R.drawable.ic_arrow_big_up_dash,
            widthRatio = widthRatio
        )

        fun backspace(fillRight: Boolean = false, widthRatio: Float = 1.5f) = KeyData(
            code = "BACKSPACE",
            displayText = "",
            keyType = KeyType.ACTION,
            iconResId = R.drawable.ic_delete,
            widthRatio = widthRatio,
            fillRight = fillRight
        )

        fun enterDynamic(imeAction: Int? = null, widthRatio: Float = 1.5f) = KeyData(
            code = "ENTER",
            displayText = "",
            keyType = KeyType.ACTION,
            iconResId = getReturnIconForImeAction(imeAction),
            widthRatio = widthRatio
        )

        private fun getReturnIconForImeAction(imeAction: Int?): Int {
            return when (imeAction) {
                EditorInfo.IME_ACTION_GO -> R.drawable.ic_arrow_right
                EditorInfo.IME_ACTION_SEARCH -> R.drawable.ic_search
                EditorInfo.IME_ACTION_SEND -> R.drawable.ic_send_horizontal
                EditorInfo.IME_ACTION_NEXT -> R.drawable.ic_arrow_right_to_line
                EditorInfo.IME_ACTION_PREVIOUS -> R.drawable.ic_arrow_left_to_line
                EditorInfo.IME_ACTION_DONE -> R.drawable.ic_check
                else -> R.drawable.ic_corner_down_left
            }
        }

        fun space() = KeyData(
            code = "SPACE",
            displayText = "",
            keyType = KeyType.SPACE,
            iconResId = R.drawable.ic_space,
            widthRatio = 4f
        )

        // Layout switch keys
        fun layoutSwitch(displayText: String) = KeyData(
            code = "LAYOUT_SWITCH",
            displayText = displayText,
            keyType = KeyType.LAYOUT_SWITCH,
            widthRatio = 1f
        )

        fun modeSwitch(displayText: String) = KeyData(
            code = when (displayText) {
                "123", "ABC", "€~\\" -> displayText
                else -> "MODE_SWITCH"
            },
            displayText = displayText,
            keyType = KeyType.MODIFIER,
            widthRatio = 1.5f
        )

        fun emojiSwitch() = KeyData(
            code = "EMOJI",
            displayText = "",
            keyType = KeyType.MODIFIER,
            iconResId = R.drawable.ic_smile
        )

        fun numpadSpace() = KeyData(
            code = "SPACE",
            displayText = "",
            keyType = KeyType.MODIFIER,
            iconResId = R.drawable.ic_space,
            widthRatio = 1f
        )

        fun phoneDigit(digit: String, hint: String) = KeyData(
            code = digit,
            displayText = digit,
            keyType = KeyType.CHARACTER,
            hintText = hint
        )

        fun spacer(widthRatio: Float = 1.5f) = KeyData(
            code = "SPACER",
            displayText = "",
            keyType = KeyType.CHARACTER,
            widthRatio = widthRatio
        )
    }
}
