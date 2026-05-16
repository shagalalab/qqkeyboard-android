package com.shagalalab.qqkeyboard.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.BuildConfig
import com.shagalalab.qqkeyboard.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("About") },
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("QqKeyboard", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Version ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "A virtual keyboard for the Karakalpak language, supporting both Latin (QQ) " +
                            "and Cyrillic (ҚҚ) scripts. Built with Jetpack Compose.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Developer", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Shagala Lab", style = MaterialTheme.typography.bodyMedium)
                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shagalalab/qqkeyboard-android2"))
                            )
                        },
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Text("View on GitHub")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Features", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "• Latin and Cyrillic Karakalpak layouts\n" +
                            "• Emoji panel with recent emoji tracking\n" +
                            "• Auto-capitalization after sentences\n" +
                            "• Double-space converts to period\n" +
                            "• Double-tap Shift for Caps Lock\n" +
                            "• Haptic and sound feedback",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
