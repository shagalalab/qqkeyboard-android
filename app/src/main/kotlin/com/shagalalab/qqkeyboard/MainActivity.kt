package com.shagalalab.qqkeyboard

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.ui.theme.QqKeyboardTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QqKeyboardTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(stringResource(R.string.app_name))
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(modifier
        .padding(16.dp)
        .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val context = LocalContext.current
        val (text, setTextValue) = remember { mutableStateOf(TextFieldValue("")) }

        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }) {
            Text(text = stringResource(R.string.enable_keyboard))
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
        }) {
            Text(text = stringResource(R.string.change_default_keyboard))
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            // TODO: go to keyboard settings
        }) {
            Text(text = stringResource(R.string.keyboard_settings))
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            // TODO: go to about screen
        }) {
            Text(text = stringResource(R.string.about_keyboard))
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            // TODO: go to help screen
        }) {
            Text(text = stringResource(R.string.help))
        }

        OutlinedTextField(
            value = text,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp),
            onValueChange = setTextValue,
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text(stringResource(R.string.test_keyboard_here)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    QqKeyboardTheme {
        MainScreen()
    }
}
