package com.shagalalab.qqkeyboard.ui.screens

import android.app.Activity
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.help.HelpActivity
import com.shagalalab.qqkeyboard.settings.ImePreferencesActivity
import com.shagalalab.qqkeyboard.settings.about.AboutActivity
import com.shagalalab.qqkeyboard.ui.components.MaterialIconButton
import com.shagalalab.qqkeyboard.util.isInputEnabled
import com.shagalalab.qqkeyboard.util.openEnableKeyboard
import kotlinx.coroutines.launch

@Composable
fun MainScreen(scaffoldState: ScaffoldState, activity: Activity) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(8.dp)) {
        MaterialIconButton(
            text = stringResource(id = R.string.enable_keyboard),
            icon = painterResource(
                id = R.drawable.ic_add_black_24dp
            )
        ) {
            activity.openEnableKeyboard()
        }

        MaterialIconButton(
            text = stringResource(id = R.string.change_default_keyboard),
            icon = painterResource(
                id = R.drawable.ic_keyboard_black_24dp
            )
        ) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (activity.isInputEnabled()) {
                imm.showInputMethodPicker()
            } else {
                scope.launch {
                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(R.string.enable_keyboard),
                        actionLabel = context.getString(R.string.enable_keyboard_first),
                        duration = SnackbarDuration.Long
                    )
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> {}
                        SnackbarResult.ActionPerformed -> { activity.openEnableKeyboard() }
                    }
                }
            }
        }

        MaterialIconButton(
            text = stringResource(id = R.string.keyboard_settings),
            icon = painterResource(id = R.drawable.ic_settings_black_24dp)
        ) {
            activity.startActivity(Intent(activity, ImePreferencesActivity::class.java))
        }

        MaterialIconButton(
            text = stringResource(id = R.string.about_keyboard),
            icon = painterResource(id = R.drawable.ic_info_outline_black_24dp)
        ) {
            activity.startActivity(Intent(activity, AboutActivity::class.java))
        }

        MaterialIconButton(
            text = stringResource(id = R.string.help),
            icon = painterResource(id = R.drawable.ic_help_outline_black_24dp)
        ) {
            activity.startActivity(Intent(activity, HelpActivity::class.java))
        }
    }

}