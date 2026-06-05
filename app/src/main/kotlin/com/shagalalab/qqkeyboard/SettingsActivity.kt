package com.shagalalab.qqkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode
import com.shagalalab.qqkeyboard.keyboard.model.VibrationStrength
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences
import com.shagalalab.qqkeyboard.ui.settings.OptionSelectionScreen
import com.shagalalab.qqkeyboard.ui.settings.SelectionOption
import com.shagalalab.qqkeyboard.ui.settings.SettingsScreen
import com.shagalalab.qqkeyboard.ui.settings.ThemeSelectionScreen
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
                        OptionSelectionScreen(
                            title = stringResource(R.string.settings_keyboard_height),
                            options = listOf(
                                SelectionOption(KeyboardHeight.SHORT, stringResource(R.string.settings_height_short)),
                                SelectionOption(KeyboardHeight.DEFAULT, stringResource(R.string.settings_height_default)),
                            ),
                            selectedValue = keyboardHeight,
                            onSelect = { height ->
                                keyboardHeight = height
                                preferences.keyboardHeight = height
                                navController.popBackStack()
                            },
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                    composable("vibration_selection") {
                        OptionSelectionScreen(
                            title = stringResource(R.string.settings_vibration_intensity),
                            options = listOf(
                                SelectionOption(VibrationStrength.LIGHT, stringResource(R.string.settings_vibration_light)),
                                SelectionOption(VibrationStrength.MEDIUM, stringResource(R.string.settings_vibration_medium)),
                                SelectionOption(VibrationStrength.STRONG, stringResource(R.string.settings_vibration_strong)),
                            ),
                            selectedValue = vibrationStrength,
                            onSelect = { strength ->
                                vibrationStrength = strength
                                preferences.vibrationStrength = strength
                                navController.popBackStack()
                            },
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                    composable("top_row_selection") {
                        OptionSelectionScreen(
                            title = stringResource(R.string.settings_top_row_layout),
                            options = listOf(
                                SelectionOption(
                                    value = TopRowMode.EXTRA_LETTERS,
                                    label = stringResource(R.string.settings_top_row_extra_letters),
                                    description = stringResource(R.string.settings_top_row_extra_letters_desc),
                                ),
                                SelectionOption(
                                    value = TopRowMode.NUMBERS,
                                    label = stringResource(R.string.settings_top_row_numbers),
                                    description = stringResource(R.string.settings_top_row_numbers_desc),
                                ),
                            ),
                            selectedValue = topRowMode,
                            onSelect = { mode ->
                                topRowMode = mode
                                preferences.topRowMode = mode
                                navController.popBackStack()
                            },
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
