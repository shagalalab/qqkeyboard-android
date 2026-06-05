package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.shagalalab.qqkeyboard.R

data class SelectionOption<T>(val value: T, val label: String, val description: String? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OptionSelectionScreen(
    title: String,
    options: List<SelectionOption<T>>,
    selectedValue: T,
    onSelect: (T) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            items(options) { option ->
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
}
