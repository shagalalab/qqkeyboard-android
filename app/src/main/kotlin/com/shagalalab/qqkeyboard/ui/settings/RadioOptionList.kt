package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class SelectionOption<T>(val value: T, val label: String, val description: String? = null)

@Composable
fun <T> RadioOptionList(
    options: List<SelectionOption<T>>,
    selectedValue: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        options.forEach { option ->
            ListItem(
                headlineContent = { Text(option.label) },
                supportingContent = option.description?.let { desc -> { Text(desc) } },
                leadingContent = {
                    RadioButton(selected = selectedValue == option.value, onClick = null)
                },
                modifier = Modifier.clickable { onSelect(option.value) }
            )
        }
    }
}
