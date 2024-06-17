package com.shagalalab.qqkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.res.stringResource
import com.shagalalab.qqkeyboard.ui.AppTheme
import com.shagalalab.qqkeyboard.ui.screens.MainScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val state = rememberScaffoldState()
                Scaffold(
                    scaffoldState = state,
                    topBar = {
                        TopAppBar(title = {
                            Text(text = stringResource(id = R.string.ime_name))
                        }
                        )
                    },
                    content = { paddingValues ->
                        paddingValues.calculateTopPadding()
                        MainScreen(scaffoldState = state, activity = this)
                    }
                )

            }
        }
    }
}