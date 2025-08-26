package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferences = remember { KeyboardPreferences(context) }
    
    var soundEnabled by remember { mutableStateOf(preferences.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(preferences.vibrationEnabled) }
    var selectedTheme by remember { mutableStateOf(preferences.selectedTheme) }
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sound Settings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                }
            }
            
            // Theme Settings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleMedium
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
                        TextButton(
                            onClick = { showThemeDialog = true }
                        ) {
                            Text("Change")
                        }
                    }
                }
            }
            
            // About Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                TextButton(
                    onClick = { showThemeDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}