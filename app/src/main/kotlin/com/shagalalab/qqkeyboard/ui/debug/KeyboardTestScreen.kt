package com.shagalalab.qqkeyboard.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardTestScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Test Keyboard") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(painterResource(R.drawable.arrow_back_24px), contentDescription = "Back")
                }
            },
            windowInsets = WindowInsets(0)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TestField(
                label = "Plain text (KeyboardType.Text + ImeAction.Done)",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                onDone = { focusManager.clearFocus() }
            )
            TestField(
                label = "Text → Next (1 of 3) (KeyboardType.Text + ImeAction.Next)",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
            TestField(
                label = "Text → Next (2 of 3) (KeyboardType.Text + ImeAction.Next)",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
            TestField(
                label = "Text → Done (3 of 3) (KeyboardType.Text + ImeAction.Done)",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                onDone = { focusManager.clearFocus() }
            )
            TestField(
                label = "Password (KeyboardType.Password + ImeAction.Done)",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                obscured = true,
                onDone = { focusManager.clearFocus() }
            )
            TestField(
                label = "PIN / Number password (KeyboardType.NumberPassword + ImeAction.Done)",
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done,
                obscured = true,
                onDone = { focusManager.clearFocus() }
            )
            TestField(
                label = "Number (KeyboardType.Number + ImeAction.Done)",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                onDone = { focusManager.clearFocus() }
            )
            TestField(
                label = "Decimal (KeyboardType.Decimal + ImeAction.Done)",
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
                onDone = { focusManager.clearFocus() }
            )
            TestField(
                label = "Phone (KeyboardType.Phone + ImeAction.Done)",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
                onDone = { focusManager.clearFocus() }
            )
            TestField(
                label = "Email (KeyboardType.Email + ImeAction.Next)",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
            TestField(
                label = "URL (KeyboardType.Uri + ImeAction.Go)",
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go,
                onDone = { focusManager.clearFocus() }
            )
            TestField(
                label = "Multiline text (KeyboardType.Text + ImeAction.Default)",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default,
                singleLine = false
            )
        }
    }
}

@Composable
private fun TestField(
    label: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    obscured: Boolean = false,
    singleLine: Boolean = true,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null,
) {
    var value by remember { mutableStateOf("") }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            visualTransformation = if (obscured) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone?.invoke() },
                onGo = { onDone?.invoke() },
                onNext = { onNext?.invoke() },
                onSearch = { onDone?.invoke() },
                onSend = { onDone?.invoke() }
            )
        )
    }
}
