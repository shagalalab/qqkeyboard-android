package com.shagalalab.qqkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences
import com.shagalalab.qqkeyboard.ui.settings.KeyboardHeightScreen
import com.shagalalab.qqkeyboard.ui.settings.SettingsScreen
import com.shagalalab.qqkeyboard.ui.settings.ThemeSelectionScreen
import com.shagalalab.qqkeyboard.ui.settings.TopRowModeScreen
import com.shagalalab.qqkeyboard.ui.settings.VibrationStrengthScreen
import com.shagalalab.qqkeyboard.ui.theme.QqKeyboardTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QqKeyboardTheme {
                val navController = rememberNavController()
                val preferences = remember { KeyboardPreferences(this@SettingsActivity) }
                var keyboardHeight by remember { mutableStateOf(preferences.keyboardHeight) }
                var vibrationStrength by remember { mutableStateOf(preferences.vibrationStrength) }
                var topRowMode by remember { mutableStateOf(preferences.topRowMode) }

                NavHost(navController = navController, startDestination = "settings") {
                    composable("settings") {
                        SettingsScreen(
                            onBackClick = { finish() },
                            onThemeSelectionClick = { navController.navigate("theme_selection") },
                            keyboardHeight = keyboardHeight,
                            onKeyboardHeightClick = { navController.navigate("height_selection") },
                            vibrationStrength = vibrationStrength,
                            onVibrationStrengthClick = { navController.navigate("vibration_selection") },
                            topRowMode = topRowMode,
                            onTopRowModeClick = { navController.navigate("top_row_selection") },
                        )
                    }
                    composable("theme_selection") {
                        ThemeSelectionScreen(onBackClick = { navController.popBackStack() })
                    }
                    composable("height_selection") {
                        KeyboardHeightScreen(
                            selectedValue = keyboardHeight,
                            onSelect = { height ->
                                keyboardHeight = height
                                preferences.keyboardHeight = height
                            },
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                    composable("vibration_selection") {
                        VibrationStrengthScreen(
                            selectedValue = vibrationStrength,
                            onSelect = { strength ->
                                vibrationStrength = strength
                                preferences.vibrationStrength = strength
                            },
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                    composable("top_row_selection") {
                        TopRowModeScreen(
                            selectedValue = topRowMode,
                            onSelect = { mode ->
                                topRowMode = mode
                                preferences.topRowMode = mode
                            },
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
