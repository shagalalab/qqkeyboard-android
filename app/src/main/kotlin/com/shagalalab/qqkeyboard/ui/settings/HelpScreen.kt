package com.shagalalab.qqkeyboard.ui.settings

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
            title = { Text("Help") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(painterResource(R.drawable.arrow_back_24px), contentDescription = "Back")
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Setup", style = MaterialTheme.typography.titleMedium)

                    Text("Step 1 — Enable the keyboard", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Go to Android Settings → System → Language & Input → On-screen keyboard, " +
                            "then enable QqKeyboard.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Input Method Settings")
                    }

                    Text("Step 2 — Set as default keyboard", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Tap the keyboard icon in the navigation bar while typing, or use the button below.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = {
                            (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                                .showInputMethodPicker()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Switch Default Keyboard")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Using the Keyboard", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Switch layout — Tap the QQ / ҚҚ button in the bottom-left corner to toggle " +
                            "between Latin and Cyrillic scripts.\n\n" +
                            "Shift — Tap once for one uppercase letter. Double-tap to enable Caps Lock. " +
                            "Tap again to turn Caps Lock off.\n\n" +
                            "Numbers & symbols — Tap the 123 button. Tap ABC to return to letters.\n\n" +
                            "Emoji — Tap the smiley face button to open the emoji panel.\n\n" +
                            "Double-space — Automatically inserts a period followed by a space, " +
                            "useful for ending sentences quickly.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tips", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "• The keyboard remembers which layout (Latin or Cyrillic) you last used.\n\n" +
                            "• The first letter of a new sentence is automatically capitalized.\n\n" +
                            "• Hold Backspace to delete characters continuously.\n\n" +
                            "• Recently used emojis appear in the Recent tab of the emoji panel.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
