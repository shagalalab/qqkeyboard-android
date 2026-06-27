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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shagalalab.qqkeyboard.ui.debug.KeyboardTestScreen
import com.shagalalab.qqkeyboard.ui.settings.AboutScreen
import com.shagalalab.qqkeyboard.ui.settings.HelpScreen
import com.shagalalab.qqkeyboard.ui.theme.QqKeyboardTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QqKeyboardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val backStack = rememberNavBackStack(Home)

                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {
                            entry<Home> {
                                MainScreen(
                                    onSettingsClick = { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) },
                                    onAboutClick = { backStack.add(About) },
                                    onHelpClick = { backStack.add(Help) },
                                    onTestKeyboardClick = { backStack.add(TestKeyboard) },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            entry<About> {
                                AboutScreen(
                                    onBackClick = { backStack.removeLastOrNull() },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            entry<Help> {
                                HelpScreen(
                                    onBackClick = { backStack.removeLastOrNull() },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            entry<TestKeyboard> {
                                KeyboardTestScreen(
                                    onBackClick = { backStack.removeLastOrNull() },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onTestKeyboardClick: () -> Unit = {},
) {
    Column(modifier.fillMaxWidth()) {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            windowInsets = WindowInsets(0)
        )

        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val context = LocalContext.current
            val (text, setTextValue) = remember { mutableStateOf(TextFieldValue("")) }

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }) {
                Text(text = stringResource(R.string.enable_keyboard), style = MaterialTheme.typography.titleMedium)
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
            }) {
                Text(text = stringResource(R.string.change_default_keyboard), style = MaterialTheme.typography.titleMedium)
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = onSettingsClick) {
                Text(text = stringResource(R.string.keyboard_settings), style = MaterialTheme.typography.titleMedium)
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = onAboutClick) {
                Text(text = stringResource(R.string.about_keyboard), style = MaterialTheme.typography.titleMedium)
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = onHelpClick) {
                Text(text = stringResource(R.string.help), style = MaterialTheme.typography.titleMedium)
            }

            OutlinedTextField(
                value = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 100.dp),
                onValueChange = setTextValue,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences),
                placeholder = { Text(stringResource(R.string.test_keyboard_here)) }
            )

            if (BuildConfig.DEBUG) {
                Button(modifier = Modifier.fillMaxWidth(), onClick = onTestKeyboardClick) {
                    Text(text = "Test keyboard", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    QqKeyboardTheme {
        MainScreen()
    }
}
