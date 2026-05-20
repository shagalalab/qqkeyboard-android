package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode
import com.shagalalab.qqkeyboard.keyboard.model.VibrationStrength
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
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
    var doubleSpacePeriodEnabled by remember { mutableStateOf(preferences.doubleSpacePeriodEnabled) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Keyboard Settings") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(painterResource(R.drawable.arrow_back_24px), contentDescription = "Back")
                }
            },
            windowInsets = WindowInsets(0)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Settings
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Input",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Takes effect next time the keyboard opens.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Auto-capitalization")
                        Switch(
                            checked = autoCapEnabled,
                            onCheckedChange = {
                                autoCapEnabled = it
                                preferences.autoCapEnabled = it
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Double-space to period")
                        Switch(
                            checked = doubleSpacePeriodEnabled,
                            onCheckedChange = {
                                doubleSpacePeriodEnabled = it
                                preferences.doubleSpacePeriodEnabled = it
                            }
                        )
                    }
                }
            }

            // Appearance Settings
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Takes effect next time the keyboard opens.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Theme")
                            Text(
                                text = selectedTheme,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { showThemeDialog = true }) {
                            Text("Change")
                        }
                    }

                    Text(
                        text = "Keyboard height",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    listOf(
                        KeyboardHeight.SHORT to "Short",
                        KeyboardHeight.DEFAULT to "Default",
                        KeyboardHeight.TALL to "Tall"
                    ).forEach { (height, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = keyboardHeight == height,
                                onClick = {
                                    keyboardHeight = height
                                    preferences.keyboardHeight = height
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Key border")
                        Switch(
                            checked = keyBorderEnabled,
                            onCheckedChange = {
                                keyBorderEnabled = it
                                preferences.keyBorderEnabled = it
                            }
                        )
                    }
                }
            }

            // Sound & Feedback Settings
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Sound & Feedback",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Key press sound")
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                preferences.soundEnabled = it
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vibration")
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = {
                                vibrationEnabled = it
                                preferences.vibrationEnabled = it
                            }
                        )
                    }

                    Text(
                        text = "Vibration intensity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (vibrationEnabled) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf(
                        VibrationStrength.LIGHT to "Light",
                        VibrationStrength.MEDIUM to "Medium",
                        VibrationStrength.STRONG to "Strong"
                    ).forEach { (strength, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = vibrationStrength == strength,
                                enabled = vibrationEnabled,
                                onClick = {
                                    vibrationStrength = strength
                                    preferences.vibrationStrength = strength
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                color = if (vibrationEnabled) MaterialTheme.colorScheme.onSurface
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Top Row Layout
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Top Row Layout",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Takes effect next time the keyboard opens.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf(
                        TopRowMode.EXTRA_LETTERS to "Extra letters (á, ǵ, ү, қ…)",
                        TopRowMode.NUMBERS to "Numbers (1–9, 0) with long-press letters"
                    ).forEach { (mode, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = topRowMode == mode,
                                onClick = {
                                    topRowMode = mode
                                    preferences.topRowMode = mode
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            }

            // About Section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Karakalpak Keyboard v1.0\n\nSupports both Latin and Cyrillic scripts for the Karakalpak language.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    KeyboardThemes.getAllThemes().forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTheme == theme.name,
                                onClick = {
                                    selectedTheme = theme.name
                                    preferences.selectedTheme = theme.name
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(theme.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
