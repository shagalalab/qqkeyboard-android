package com.shagalalab.qqkeyboard.keyboard.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

data class KeyboardTheme(
    val name: String,
    val colorScheme: ColorScheme,
    val keyBackground: Color,
    val keyBorder: Color,
    val keyText: Color,
    val modifierKeyBackground: Color,
    val modifierKeyBorder: Color,
    val modifierKeyText: Color,
    val spaceKeyBackground: Color,
    val layoutSwitchBackground: Color,
    val pressedKeyBackground: Color,
    val activeModifierBackground: Color
)

object KeyboardThemes {
    val Light = KeyboardTheme(
        name = "Light",
        colorScheme = lightColorScheme(),
        keyBackground = Color(0xFFFFFFFF),
        keyBorder = Color(0xFFE0E0E0),
        keyText = Color(0xFF000000),
        modifierKeyBackground = Color(0xFFF5F5F5),
        modifierKeyBorder = Color(0xFFBDBDBD),
        modifierKeyText = Color(0xFF424242),
        spaceKeyBackground = Color(0xFFF9F9F9),
        layoutSwitchBackground = Color(0xFFE8F5E8),
        pressedKeyBackground = Color(0xFFE3F2FD),
        activeModifierBackground = Color(0xFF2196F3)
    )
    
    val Dark = KeyboardTheme(
        name = "Dark",
        colorScheme = darkColorScheme(),
        keyBackground = Color(0xFF2C2C2C),
        keyBorder = Color(0xFF404040),
        keyText = Color(0xFFFFFFFF),
        modifierKeyBackground = Color(0xFF363636),
        modifierKeyBorder = Color(0xFF505050),
        modifierKeyText = Color(0xFFE0E0E0),
        spaceKeyBackground = Color(0xFF242424),
        layoutSwitchBackground = Color(0xFF1B4B1B),
        pressedKeyBackground = Color(0xFF1976D2),
        activeModifierBackground = Color(0xFF1976D2)
    )
    
    val KarakalpakBlue = KeyboardTheme(
        name = "Karakalpak Blue",
        colorScheme = lightColorScheme(
            primary = Color(0xFF0066CC),
            secondary = Color(0xFF4A90E2)
        ),
        keyBackground = Color(0xFFF8FAFF),
        keyBorder = Color(0xFFD1E2FF),
        keyText = Color(0xFF1A1A1A),
        modifierKeyBackground = Color(0xFFE8F2FF),
        modifierKeyBorder = Color(0xFF0066CC),
        modifierKeyText = Color(0xFF0066CC),
        spaceKeyBackground = Color(0xFFF0F7FF),
        layoutSwitchBackground = Color(0xFFE0F0FF),
        pressedKeyBackground = Color(0xFFB3D9FF),
        activeModifierBackground = Color(0xFF0066CC)
    )
    
    fun getDefaultTheme(): KeyboardTheme = Light
    
    fun getAllThemes(): List<KeyboardTheme> = listOf(Light, Dark, KarakalpakBlue)
}