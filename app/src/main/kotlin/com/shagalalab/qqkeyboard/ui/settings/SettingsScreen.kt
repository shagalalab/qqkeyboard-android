package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode
import com.shagalalab.qqkeyboard.keyboard.model.VibrationStrength
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onThemeSelectionClick: () -> Unit,
    keyboardHeight: KeyboardHeight,
    onKeyboardHeightClick: () -> Unit,
    vibrationStrength: VibrationStrength,
    onVibrationStrengthClick: () -> Unit,
    topRowMode: TopRowMode,
    onTopRowModeClick: () -> Unit,
) {
    val context = LocalContext.current
    val preferences = remember { KeyboardPreferences(context) }

    var soundEnabled by remember { mutableStateOf(preferences.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(preferences.vibrationEnabled) }
    var selectedTheme by remember { mutableStateOf(preferences.selectedTheme) }
    var keyBorderEnabled by remember { mutableStateOf(preferences.keyBorderEnabled) }
    var autoCapEnabled by remember { mutableStateOf(preferences.autoCapEnabled) }
    var autoSpaceAfterPunctuation by remember { mutableStateOf(preferences.autoSpaceAfterPunctuation) }
    var doubleSpacePeriodEnabled by remember { mutableStateOf(preferences.doubleSpacePeriodEnabled) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_keyboard_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.arrow_back_24px), contentDescription = stringResource(R.string.cd_back))
                    }
                },
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Settings
            Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                SegmentedListItem(
                    onClick = {
                        autoCapEnabled = !autoCapEnabled
                        preferences.autoCapEnabled = autoCapEnabled
                    },
                    shapes = ListItemDefaults.segmentedShapes(0, 3),
                    trailingContent = {
                        Switch(checked = autoCapEnabled, onCheckedChange = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_auto_cap))
                }
                SegmentedListItem(
                    onClick = {
                        autoSpaceAfterPunctuation = !autoSpaceAfterPunctuation
                        preferences.autoSpaceAfterPunctuation = autoSpaceAfterPunctuation
                    },
                    shapes = ListItemDefaults.segmentedShapes(1, 3),
                    trailingContent = {
                        Switch(checked = autoSpaceAfterPunctuation, onCheckedChange = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_auto_space))
                }
                SegmentedListItem(
                    onClick = {
                        doubleSpacePeriodEnabled = !doubleSpacePeriodEnabled
                        preferences.doubleSpacePeriodEnabled = doubleSpacePeriodEnabled
                    },
                    shapes = ListItemDefaults.segmentedShapes(2, 3),
                    trailingContent = {
                        Switch(checked = doubleSpacePeriodEnabled, onCheckedChange = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_double_space_period))
                }
            }

            // Appearance Settings
            Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                SegmentedListItem(
                    onClick = onThemeSelectionClick,
                    shapes = ListItemDefaults.segmentedShapes(0, 4),
                    supportingContent = { Text(selectedTheme) },
                    trailingContent = {
                        Icon(painter = painterResource(R.drawable.chevron_right_24px), contentDescription = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_theme))
                }
                SegmentedListItem(
                    onClick = onKeyboardHeightClick,
                    shapes = ListItemDefaults.segmentedShapes(1, 4),
                    supportingContent = {
                        Text(
                            when (keyboardHeight) {
                                KeyboardHeight.SHORT -> stringResource(R.string.settings_height_short)
                                KeyboardHeight.DEFAULT -> stringResource(R.string.settings_height_default)
                            }
                        )
                    },
                    trailingContent = {
                        Icon(painter = painterResource(R.drawable.chevron_right_24px), contentDescription = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_keyboard_height))
                }
                SegmentedListItem(
                    onClick = {
                        keyBorderEnabled = !keyBorderEnabled
                        preferences.keyBorderEnabled = keyBorderEnabled
                    },
                    shapes = ListItemDefaults.segmentedShapes(2, 4),
                    trailingContent = {
                        Switch(checked = keyBorderEnabled, onCheckedChange = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_key_border))
                }
                SegmentedListItem(
                    onClick = onTopRowModeClick,
                    shapes = ListItemDefaults.segmentedShapes(3, 4),
                    supportingContent = {
                        Text(
                            when (topRowMode) {
                                TopRowMode.EXTRA_LETTERS -> stringResource(R.string.settings_top_row_extra_letters)
                                TopRowMode.NUMBERS -> stringResource(R.string.settings_top_row_numbers)
                            }
                        )
                    },
                    trailingContent = {
                        Icon(painter = painterResource(R.drawable.chevron_right_24px), contentDescription = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_top_row_layout))
                }
            }

            // Sound & Feedback Settings
            Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                SegmentedListItem(
                    onClick = {
                        soundEnabled = !soundEnabled
                        preferences.soundEnabled = soundEnabled
                    },
                    shapes = ListItemDefaults.segmentedShapes(0, 3),
                    trailingContent = {
                        Switch(checked = soundEnabled, onCheckedChange = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_key_press_sound))
                }
                SegmentedListItem(
                    onClick = {
                        vibrationEnabled = !vibrationEnabled
                        preferences.vibrationEnabled = vibrationEnabled
                    },
                    shapes = ListItemDefaults.segmentedShapes(1, 3),
                    trailingContent = {
                        Switch(checked = vibrationEnabled, onCheckedChange = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_vibration))
                }
                SegmentedListItem(
                    onClick = onVibrationStrengthClick,
                    shapes = ListItemDefaults.segmentedShapes(2, 3),
                    enabled = vibrationEnabled,
                    supportingContent = {
                        Text(
                            text = when (vibrationStrength) {
                                VibrationStrength.LIGHT -> stringResource(R.string.settings_vibration_light)
                                VibrationStrength.MEDIUM -> stringResource(R.string.settings_vibration_medium)
                                VibrationStrength.STRONG -> stringResource(R.string.settings_vibration_strong)
                            },
                            color = if (vibrationEnabled) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    trailingContent = {
                        Icon(painter = painterResource(R.drawable.chevron_right_24px), contentDescription = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer, disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Text(
                        text = stringResource(R.string.settings_vibration_intensity),
                        color = if (vibrationEnabled) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
