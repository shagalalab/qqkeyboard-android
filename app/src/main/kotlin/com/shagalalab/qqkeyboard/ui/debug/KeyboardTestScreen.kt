package com.shagalalab.qqkeyboard.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.R

private val keyboardTypes = listOf(
    "Text" to KeyboardType.Text,
    "Number" to KeyboardType.Number,
    "Phone" to KeyboardType.Phone,
    "Uri" to KeyboardType.Uri,
    "Email" to KeyboardType.Email,
    "Password" to KeyboardType.Password,
    "NumberPassword" to KeyboardType.NumberPassword,
    "Decimal" to KeyboardType.Decimal,
)

private val imeActions = listOf(
    "Default" to ImeAction.Default,
    "None" to ImeAction.None,
    "Go" to ImeAction.Go,
    "Search" to ImeAction.Search,
    "Send" to ImeAction.Send,
    "Next" to ImeAction.Next,
    "Previous" to ImeAction.Previous,
    "Done" to ImeAction.Done,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardTestScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var selectedKeyboardType by remember { mutableStateOf(keyboardTypes[0]) }
    var selectedImeAction by remember { mutableStateOf(imeActions[0]) }
    var fieldValue by remember(selectedKeyboardType, selectedImeAction) { mutableStateOf("") }

    val isObscured = selectedKeyboardType.second == KeyboardType.Password ||
            selectedKeyboardType.second == KeyboardType.NumberPassword

    Column(modifier = modifier.fillMaxSize()) {
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
            OutlinedTextField(
                value = fieldValue,
                onValueChange = { fieldValue = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (isObscured) PasswordVisualTransformation() else VisualTransformation.None,
                placeholder = { Text("Enter your text here") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = selectedKeyboardType.second,
                    imeAction = selectedImeAction.second
                ),
                keyboardActions = KeyboardActions(
                    onAny = { focusManager.clearFocus() }
                )
            )

            LabeledDropdown(
                label = "KeyboardType",
                options = keyboardTypes.map { it.first },
                selected = selectedKeyboardType.first,
                onSelect = { name -> selectedKeyboardType = keyboardTypes.first { it.first == name } }
            )

            LabeledDropdown(
                label = "ImeAction",
                options = imeActions.map { it.first },
                selected = selectedImeAction.first,
                onSelect = { name -> selectedImeAction = imeActions.first { it.first == name } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabeledDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
