package com.shagalalab.qqkeyboard.keyboard.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object KeyboardDimensions {
    // --- Grid & Layout ---
    val rowGap = 8.dp
    val gridHorizontalPadding = 2.dp
    val gridVerticalPadding = 4.dp
    val keyHorizontalPadding = 2.dp

    // --- Key Heights ---
    val keyHeightShort = 40.dp
    val keyHeightDefault = 48.dp

    // --- Key Shapes ---
    val keyCornerRadius = 6.dp
    val bubbleCornerRadius = 8.dp

    // --- Key Font Sizes ---
    val characterKeyFontSize = 26.sp
    val modifierKeyFontSize = 18.sp
    val actionKeyFontSize = 14.sp

    // --- Key with Hint Text ---
    val hintKeyFontSize = 20.sp
    val hintKeySpacing = 6.dp
    val hintFontSize = 10.sp
    val hintLetterSpacing = 0.5.sp

    // --- Secondary Label (top-right corner) ---
    val secondaryLabelFontSize = 9.sp
    val secondaryLabelEndPadding = 5.dp
    val secondaryLabelTopPadding = 3.dp

    // --- Bubble (long-press popup) ---
    val bubbleFontSize = 26.sp
    val bubbleVerticalOffset = 6.dp
    val bubbleShadowElevation = 6.dp
    val bubbleHorizontalPadding = 10.dp

    // --- Icons ---
    val actionIconSize = 24.dp

    // --- Suggestion Strip ---
    val suggestionStripHeight = 40.dp
    val suggestionStripHorizontalPadding = 8.dp
    val suggestionPaddingHorizontal = 8.dp
    val suggestionPaddingVertical = 8.dp
    val suggestionFontSize = 18.sp
    val emojiTogglePadding = 4.dp
    val emojiToggleIconSize = 24.dp

    // --- Emoji Layout ---
    const val emojiGridColumns = 9
    val emojiKeyHeight = 48.dp
    val emojiFontSize = 28.sp
    val categoryIconSize = 24.dp
    val categoryIconTouchSize = categoryIconSize + 4.dp
    val categoryVerticalPadding = 6.dp
    val categoryNavHorizontalPadding = 8.dp
    val categoryClosePadding = 4.dp
}
