package com.shagalalab.qqkeyboard.keyboard.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight

data class KeyboardColors(
    val keyboardBackground: Color,
    val keyBackground: Color,
    val keyContent: Color,
    val modifierBackground: Color,
    val modifierContent: Color,
    val pressedBackground: Color,
    val shiftActiveBackground: Color,
    val shiftActiveContent: Color,
    val bubbleBackground: Color,
)

data class KeyboardTheme(
    val name: String,
    val colors: KeyboardColors,
)

val LocalKeyboardColors = staticCompositionLocalOf { KeyboardThemes.Light.colors }
val LocalKeyboardHeight = staticCompositionLocalOf { KeyboardHeight.DEFAULT }
val LocalKeyboardBorderEnabled = staticCompositionLocalOf { true }

fun KeyboardHeight.toDp(): Dp = when (this) {
    KeyboardHeight.SHORT -> 40.dp
    KeyboardHeight.DEFAULT -> 48.dp
}

object KeyboardThemes {

    val Light = KeyboardTheme(
        name = "Light",
        colors = KeyboardColors(
            keyboardBackground = Color(0xFFEBF0F3),
            keyBackground = Color(0xFFFFFFFF),
            keyContent = Color(0xFF000000),
            modifierBackground = Color(0xFFC4C9CC),
            modifierContent = Color(0xBF000000),
            pressedBackground = Color(0xFFEBF0F3),
            shiftActiveBackground = Color(0xFF4A90D9),
            shiftActiveContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFFD1D6D9),
        )
    )

    val Dark = KeyboardTheme(
        name = "Dark",
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF19191A),
            keyBackground = Color(0xFF2E3133),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF222426),
            modifierContent = Color(0xBFFFFFFF),
            pressedBackground = Color(0xFF19191A),
            shiftActiveBackground = Color(0xFF1976D2),
            shiftActiveContent = Color(0xFFFFFFFF),
            bubbleBackground = Color(0xFF2E3133),
        )
    )

    val Amoled = KeyboardTheme(
        name = "Amoled",
        colors = KeyboardColors(
            keyboardBackground = Color(0xFF000000),
            keyBackground = Color(0xFF161616),
            keyContent = Color(0xFFFFFFFF),
            modifierBackground = Color(0xFF0A0A0A),
            modifierContent = Color(0xFFAAAAAA),
            pressedBackground = Color(0xFF0D47A1),
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
            keyContent = Color(0xFFCDE8FF),
            modifierBackground = Color(0xFF0A1E30),
            modifierContent = Color(0xFF8AB8D8),
            pressedBackground = Color(0xFF005F9E),
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
