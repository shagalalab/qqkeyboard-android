package com.shagalalab.qqkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import com.shagalalab.qqkeyboard.ui.settings.SettingsScreen
import com.shagalalab.qqkeyboard.ui.theme.QqKeyboardTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QqKeyboardTheme {
                SettingsScreen(
                    onBackClick = { finish() },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                )
            }
        }
    }
}
