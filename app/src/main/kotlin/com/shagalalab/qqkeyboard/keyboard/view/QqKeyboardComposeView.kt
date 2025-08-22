package com.shagalalab.qqkeyboard.keyboard.view

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import com.shagalalab.qqkeyboard.keyboard.compose.QqKeyboard
import com.shagalalab.qqkeyboard.ui.theme.QqKeyboardTheme

class QqKeyboardComposeView(context: Context) : AbstractComposeView(context) {
    @Composable
    override fun Content() {
        QqKeyboardTheme {
            QqKeyboard()
        }
    }
}
