package com.shagalalab.qqkeyboard.ui.settings

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.title_help)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(painterResource(R.drawable.arrow_back_24px), contentDescription = stringResource(R.string.cd_back))
                }
            },
            windowInsets = WindowInsets(0)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.help_setup_title), style = MaterialTheme.typography.titleLarge)

                    Text(stringResource(R.string.help_step1_title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.help_step1_description),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.help_open_input_method_settings), style = MaterialTheme.typography.titleMedium)
                    }

                    Text(stringResource(R.string.help_step2_title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.help_step2_description),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = {
                            (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                                .showInputMethodPicker()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.help_switch_default_keyboard), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.help_using_keyboard_title), style = MaterialTheme.typography.titleLarge)
                    Text(
                        stringResource(R.string.help_using_keyboard_description),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.help_settings_title), style = MaterialTheme.typography.titleLarge)

                    Text(stringResource(R.string.help_settings_input_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.help_settings_input_description), style = MaterialTheme.typography.bodyLarge)

                    Text(stringResource(R.string.help_settings_appearance_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.help_settings_appearance_description), style = MaterialTheme.typography.bodyLarge)

                    Text(stringResource(R.string.help_settings_feedback_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.help_settings_feedback_description), style = MaterialTheme.typography.bodyLarge)
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.help_tips_title), style = MaterialTheme.typography.titleLarge)
                    Text(
                        stringResource(R.string.help_tips_description),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
