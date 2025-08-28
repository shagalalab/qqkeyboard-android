package com.shagalalab.qqkeyboard.keyboard.model

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
    val longPressCode: String? = null,
    val alternativeChars: List<String> = emptyList()
) {
    companion object {
        // Character keys
        fun character(code: String, displayText: String = code) = KeyData(
            code = code,
            displayText = displayText,
            keyType = KeyType.CHARACTER
        )

        // Modifier keys
        fun shift() = KeyData(
            code = "SHIFT",
            displayText = "",
            keyType = KeyType.MODIFIER,
            iconResId = R.drawable.ic_arrow_big_up_dash,
            widthRatio = 1.5f
        )

        fun backspace() = KeyData(
            code = "BACKSPACE",
            displayText = "",
            keyType = KeyType.ACTION,
            iconResId = R.drawable.ic_delete,
            widthRatio = 1.5f
        )

        fun enter() = KeyData(
            code = "ENTER",
            displayText = "",
            keyType = KeyType.ACTION,
            iconResId = R.drawable.ic_corner_down_left,
            widthRatio = 1.5f
        )

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
    }
}
