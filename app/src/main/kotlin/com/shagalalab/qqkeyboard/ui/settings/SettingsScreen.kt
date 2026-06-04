package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardThemes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val preferences = remember { KeyboardPreferences(context) }

    var soundEnabled by remember { mutableStateOf(preferences.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(preferences.vibrationEnabled) }
    var vibrationStrength by remember { mutableStateOf(preferences.vibrationStrength) }
    var selectedTheme by remember { mutableStateOf(preferences.selectedTheme) }
    var keyboardHeight by remember { mutableStateOf(preferences.keyboardHeight) }
    var keyBorderEnabled by remember { mutableStateOf(preferences.keyBorderEnabled) }
    var topRowMode by remember { mutableStateOf(preferences.topRowMode) }
    var autoCapEnabled by remember { mutableStateOf(preferences.autoCapEnabled) }
    var autoSpaceAfterPunctuation by remember { mutableStateOf(preferences.autoSpaceAfterPunctuation) }
    var doubleSpacePeriodEnabled by remember { mutableStateOf(preferences.doubleSpacePeriodEnabled) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showHeightDialog by remember { mutableStateOf(false) }
    var showVibrationStrengthDialog by remember { mutableStateOf(false) }
    var showTopRowDialog by remember { mutableStateOf(false) }

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
                        Switch(
                            checked = autoCapEnabled,
                            onCheckedChange = null
                        )
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
                        Switch(
                            checked = autoSpaceAfterPunctuation,
                            onCheckedChange = null
                        )
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
                        Switch(
                            checked = doubleSpacePeriodEnabled,
                            onCheckedChange = null
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_double_space_period))
                }
            }

            // Appearance Settings
            Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                SegmentedListItem(
                    onClick = {},
                    shapes = ListItemDefaults.segmentedShapes(0, 4),
                    supportingContent = { Text(selectedTheme) },
                    trailingContent = {
                        TextButton(onClick = { showThemeDialog = true }) {
                            Text(stringResource(R.string.settings_change))
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_theme))
                }
                SegmentedListItem(
                    onClick = {},
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
                        TextButton(onClick = { showHeightDialog = true }) {
                            Text(stringResource(R.string.settings_change))
                        }
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
                        Switch(
                            checked = keyBorderEnabled,
                            onCheckedChange = null
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_key_border))
                }
                SegmentedListItem(
                    onClick = {},
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
                        TextButton(onClick = { showTopRowDialog = true }) {
                            Text(stringResource(R.string.settings_change))
                        }
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
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = null
                        )
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
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = null
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.settings_vibration))
                }
                SegmentedListItem(
                    onClick = {},
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
                        TextButton(
                            onClick = { showVibrationStrengthDialog = true },
                            enabled = vibrationEnabled
                        ) {
                            Text(stringResource(R.string.settings_change))
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = stringResource(R.string.settings_vibration_intensity),
                        color = if (vibrationEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Keyboard Height Dialog
        if (showHeightDialog) {
            AlertDialog(
                onDismissRequest = { showHeightDialog = false },
                title = { Text(stringResource(R.string.settings_keyboard_height)) },
                text = {
                    Column {
                        listOf(
                            KeyboardHeight.SHORT to stringResource(R.string.settings_height_short),
                            KeyboardHeight.DEFAULT to stringResource(R.string.settings_height_default)
                        ).forEach { (height, label) ->
                            ListItem(
                                headlineContent = { Text(label) },
                                leadingContent = {
                                    RadioButton(selected = keyboardHeight == height, onClick = null)
                                },
                                modifier = Modifier.clickable {
                                    keyboardHeight = height
                                    preferences.keyboardHeight = height
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHeightDialog = false }) { Text(stringResource(R.string.dialog_ok)) }
                }
            )
        }
    }

    // Vibration Strength Dialog
    if (showVibrationStrengthDialog) {
        AlertDialog(
            onDismissRequest = { showVibrationStrengthDialog = false },
            title = { Text(stringResource(R.string.settings_vibration_intensity)) },
            text = {
                Column {
                    listOf(
                        VibrationStrength.LIGHT to stringResource(R.string.settings_vibration_light),
                        VibrationStrength.MEDIUM to stringResource(R.string.settings_vibration_medium),
                        VibrationStrength.STRONG to stringResource(R.string.settings_vibration_strong)
                    ).forEach { (strength, label) ->
                        ListItem(
                            headlineContent = { Text(label) },
                            leadingContent = {
                                RadioButton(selected = vibrationStrength == strength, onClick = null)
                            },
                            modifier = Modifier.clickable {
                                vibrationStrength = strength
                                preferences.vibrationStrength = strength
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVibrationStrengthDialog = false }) { Text(stringResource(R.string.dialog_ok)) }
            }
        )
    }

    // Top Row Mode Dialog
    if (showTopRowDialog) {
        AlertDialog(
            onDismissRequest = { showTopRowDialog = false },
            title = { Text(stringResource(R.string.settings_top_row_layout)) },
            text = {
                Column {
                    listOf(
                        TopRowMode.EXTRA_LETTERS to stringResource(R.string.settings_top_row_extra_letters_desc),
                        TopRowMode.NUMBERS to stringResource(R.string.settings_top_row_numbers_desc)
                    ).forEach { (mode, label) ->
                        ListItem(
                            headlineContent = { Text(label) },
                            leadingContent = {
                                RadioButton(selected = topRowMode == mode, onClick = null)
                            },
                            modifier = Modifier.clickable {
                                topRowMode = mode
                                preferences.topRowMode = mode
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTopRowDialog = false }) { Text(stringResource(R.string.dialog_ok)) }
            }
        )
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.dialog_title_select_theme)) },
            text = {
                Column {
                    KeyboardThemes.getAllThemes().forEach { theme ->
                        ListItem(
                            headlineContent = { Text(theme.name) },
                            leadingContent = {
                                RadioButton(
                                    selected = selectedTheme == theme.name,
                                    onClick = null
                                )
                            },
                            modifier = Modifier.clickable {
                                selectedTheme = theme.name
                                preferences.selectedTheme = theme.name
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(stringResource(R.string.dialog_ok))
                }
            }
        )
    }
}
