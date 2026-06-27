package com.shagalalab.qqkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences
import com.shagalalab.qqkeyboard.ui.settings.HeightSelection
import com.shagalalab.qqkeyboard.ui.settings.KeyboardHeightScreen
import com.shagalalab.qqkeyboard.ui.settings.Settings
import com.shagalalab.qqkeyboard.ui.settings.SettingsScreen
import com.shagalalab.qqkeyboard.ui.settings.ThemeSelection
import com.shagalalab.qqkeyboard.ui.settings.ThemeSelectionScreen
import com.shagalalab.qqkeyboard.ui.settings.TopRowModeScreen
import com.shagalalab.qqkeyboard.ui.settings.TopRowSelection
import com.shagalalab.qqkeyboard.ui.settings.VibrationSelection
import com.shagalalab.qqkeyboard.ui.settings.VibrationStrengthScreen
import com.shagalalab.qqkeyboard.ui.theme.QqKeyboardTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QqKeyboardTheme {
                val backStack = rememberNavBackStack(Settings)
                val preferences = remember { KeyboardPreferences(this@SettingsActivity) }
                var keyboardHeight by remember { mutableStateOf(preferences.keyboardHeight) }
                var vibrationStrength by remember { mutableStateOf(preferences.vibrationStrength) }
                var topRowMode by remember { mutableStateOf(preferences.topRowMode) }

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<Settings> {
                            SettingsScreen(
                                onBackClick = { finish() },
                                onThemeSelectionClick = { backStack.add(ThemeSelection) },
                                keyboardHeight = keyboardHeight,
                                onKeyboardHeightClick = { backStack.add(HeightSelection) },
                                vibrationStrength = vibrationStrength,
                                onVibrationStrengthClick = { backStack.add(VibrationSelection) },
                                topRowMode = topRowMode,
                                onTopRowModeClick = { backStack.add(TopRowSelection) },
                            )
                        }
                        entry<ThemeSelection> {
                            ThemeSelectionScreen(onBackClick = { backStack.removeLastOrNull() })
                        }
                        entry<HeightSelection> {
                            KeyboardHeightScreen(
                                selectedValue = keyboardHeight,
                                onSelect = { height ->
                                    keyboardHeight = height
                                    preferences.keyboardHeight = height
                                },
                                onBackClick = { backStack.removeLastOrNull() },
                            )
                        }
                        entry<VibrationSelection> {
                            VibrationStrengthScreen(
                                selectedValue = vibrationStrength,
                                onSelect = { strength ->
                                    vibrationStrength = strength
                                    preferences.vibrationStrength = strength
                                },
                                onBackClick = { backStack.removeLastOrNull() },
                            )
                        }
                        entry<TopRowSelection> {
                            TopRowModeScreen(
                                selectedValue = topRowMode,
                                onSelect = { mode ->
                                    topRowMode = mode
                                    preferences.topRowMode = mode
                                },
                                onBackClick = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                )
            }
        }
    }
}
