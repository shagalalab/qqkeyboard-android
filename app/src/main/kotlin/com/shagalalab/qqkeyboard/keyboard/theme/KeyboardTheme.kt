package com.shagalalab.qqkeyboard.keyboard.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight

data class KeyboardColors(
    val keyboardBackground: Color,
    val keyBackground: Color,
    val keyContent: Color,
    val modifierBackground: Color,
    val modifierContent: Color,
    val bubbleBackground: Color,
)

enum class KeyboardThemes(val colors: KeyboardColors) {
    Light(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFFEBF0F3),
            keyBackground = Color(0xFFFFFFFF),
            keyContent = Color(0xFF000000),
            modifierBackground = Color(0xFFC6CACC),
            modifierContent = Color(0xFF313233),
            bubbleBackground = Color(0xFFEBF0F3),
        )
    ),
    SystemAuto(colors = Light.colors),
    Dark(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF1A1A1A),
            keyBackground = Color(0xFF313233),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF252626),
            modifierContent = Color(0xFFF7FCFF),
            bubbleBackground = Color(0xFF3D3F40),
        )
    ),
    Lavender(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFFEEF0FF),
            keyBackground = Color(0xFFFFFFFF),
            keyContent = Color(0xFF000000),
            modifierBackground = Color(0xFFDCE1FF),
            modifierContent = Color(0xFF313233),
            bubbleBackground = Color(0xFFEEF0FF),
        )
    ),
    Desert(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFFF9EFE5),
            keyBackground = Color(0xFFFFFFFF),
            keyContent = Color(0xFF000000),
            modifierBackground = Color(0xFFFADDBB),
            modifierContent = Color(0xFF313233),
            bubbleBackground = Color(0xFFF9EFE5),
        )
    ),
    Rose(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFFFFE0EC),
            keyBackground = Color(0xFFFFFFFF),
            keyContent = Color(0xFF000000),
            modifierBackground = Color(0xFFF5B8CC),
            modifierContent = Color(0xFF313233),
            bubbleBackground = Color(0xFFFFE0EC),
        )
    ),
    Mint(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFFDDFAEB),
            keyBackground = Color(0xFFFFFFFF),
            keyContent = Color(0xFF000000),
            modifierBackground = Color(0xFFA8DFBF),
            modifierContent = Color(0xFF313233),
            bubbleBackground = Color(0xFFDDFAEB),
        )
    ),
    Arctic(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFFD9EEFF),
            keyBackground = Color(0xFFFFFFFF),
            keyContent = Color(0xFF000000),
            modifierBackground = Color(0xFFA8D4F0),
            modifierContent = Color(0xFF313233),
            bubbleBackground = Color(0xFFD9EEFF),
        )
    ),
    Butter(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFFFFF8D6),
            keyBackground = Color(0xFFFFFFFF),
            keyContent = Color(0xFF000000),
            modifierBackground = Color(0xFFF5DF8A),
            modifierContent = Color(0xFF313233),
            bubbleBackground = Color(0xFFFFF8D6),
        )
    ),
    Sea(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF2A4077),
            keyBackground = Color(0xFF435792),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF354B88),
            modifierContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF2A4077),
        )
    ),
    MidnightBlue(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF3C3D4F),
            keyBackground = Color(0xFF5A5C6B),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF444B5B),
            modifierContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF3C3D4F),
        )
    ),
    Forest(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF1E3A2A),
            keyBackground = Color(0xFF2C4E3A),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF183020),
            modifierContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF1E3A2A),
        )
    ),
    Mocha(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF2C1F1A),
            keyBackground = Color(0xFF4A3028),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF221610),
            modifierContent = Color(0xFFF7E8DF),
            bubbleBackground = Color(0xFF2C1F1A),
        )
    ),
    Purple(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF2A1F3D),
            keyBackground = Color(0xFF3D2D5C),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF1F1530),
            modifierContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF2A1F3D),
        )
    ),
    Burgundy(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF3D1A20),
            keyBackground = Color(0xFF5C2830),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF2E1018),
            modifierContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF3D1A20),
        )
    ),
    AMOLED(
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF000000),
            keyBackground = Color(0xFF1A1A1A),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF111111),
            modifierContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF000000),
        )
    );

    fun resolvedColors(isDark: Boolean): KeyboardColors = when (this) {
        SystemAuto -> if (isDark) Dark.colors else Light.colors
        else -> colors
    }

    companion object {
        fun getByName(name: String): KeyboardThemes = entries.firstOrNull { it.name == name } ?: Light
    }
}

val LocalKeyboardColors = staticCompositionLocalOf { KeyboardThemes.Light.colors }
val LocalKeyboardHeight = staticCompositionLocalOf { KeyboardHeight.DEFAULT }
val LocalKeyboardBorderEnabled = staticCompositionLocalOf { true }

fun KeyboardHeight.toDp(): Dp = when (this) {
    KeyboardHeight.SHORT -> KeyboardDimensions.keyHeightShort
    KeyboardHeight.DEFAULT -> KeyboardDimensions.keyHeightDefault
    KeyboardHeight.LANDSCAPE -> KeyboardDimensions.keyHeightLandscape
}
