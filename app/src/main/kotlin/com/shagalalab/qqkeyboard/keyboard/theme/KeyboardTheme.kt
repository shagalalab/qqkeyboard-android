package com.shagalalab.qqkeyboard.keyboard.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class KeyboardColors(
    val keyboardBackground: Color,
    val keyBackground: Color,
    val keyBorder: Color,
    val keyContent: Color,
    val modifierBackground: Color,
    val modifierBorder: Color,
    val modifierContent: Color,
    val pressedBackground: Color,
    val pressedBorder: Color,
    val shiftActiveBackground: Color,
    val shiftActiveContent: Color,
    val bubbleBackground: Color,
)

data class KeyboardTheme(
    val name: String,
    val colors: KeyboardColors,
)

val LocalKeyboardColors = staticCompositionLocalOf { KeyboardThemes.Light.colors }

object KeyboardThemes {

    val Light = KeyboardTheme(
        name = "Light",
        colors = KeyboardColors(
            keyboardBackground = Color(0xFFEFF0F3),
            keyBackground = Color(0xFFFFFFFF),
            keyBorder = Color(0xFFD1D3D8),
            keyContent = Color(0xFF1A1A1A),
            modifierBackground = Color(0xFFD1D3D8),
            modifierBorder = Color(0xFFB0B3BA),
            modifierContent = Color(0xFF1A1A1A),
            pressedBackground = Color(0xFFBBD3FA),
            pressedBorder = Color(0xFF4A90D9),
            shiftActiveBackground = Color(0xFF4A90D9),
            shiftActiveContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFFE0ECFF),
        )
    )

    val Dark = KeyboardTheme(
        name = "Dark",
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF1C1C1E),
            keyBackground = Color(0xFF2C2C2E),
            keyBorder = Color(0xFF3A3A3C),
            keyContent = Color(0xFFE8E8EA),
            modifierBackground = Color(0xFF1C1C1E),
            modifierBorder = Color(0xFF3A3A3C),
            modifierContent = Color(0xFFAEAEB2),
            pressedBackground = Color(0xFF1565C0),
            pressedBorder = Color(0xFF1976D2),
            shiftActiveBackground = Color(0xFF1976D2),
            shiftActiveContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF3A3A3C),
        )
    )

    val Amoled = KeyboardTheme(
        name = "Amoled",
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF000000),
            keyBackground = Color(0xFF161616),
            keyBorder = Color(0xFF242424),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF0A0A0A),
            modifierBorder = Color(0xFF242424),
            modifierContent = Color(0xFFAAAAAA),
            pressedBackground = Color(0xFF0D47A1),
            pressedBorder = Color(0xFF1565C0),
            shiftActiveBackground = Color(0xFF1565C0),
            shiftActiveContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF222222),
        )
    )

    val Ocean = KeyboardTheme(
        name = "Ocean",
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF0D2137),
            keyBackground = Color(0xFF15304D),
            keyBorder = Color(0xFF1E4570),
            keyContent = Color(0xFFCDE8FF),
            modifierBackground = Color(0xFF0A1E30),
            modifierBorder = Color(0xFF1E4570),
            modifierContent = Color(0xFF8AB8D8),
            pressedBackground = Color(0xFF005F9E),
            pressedBorder = Color(0xFF0079C8),
            shiftActiveBackground = Color(0xFF0079C8),
            shiftActiveContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF1D4A6E),
        )
    )

    fun getByName(name: String): KeyboardTheme =
        getAllThemes().firstOrNull { it.name == name } ?: Light

    fun getDefaultTheme(): KeyboardTheme = Light

    fun getAllThemes(): List<KeyboardTheme> = listOf(Light, Dark, Amoled, Ocean)
}
