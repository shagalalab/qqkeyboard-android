package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.DefaultLayoutMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultLayoutScreen(
    selectedValue: DefaultLayoutMode,
    onSelect: (DefaultLayoutMode) -> Unit,
    onBackClick: () -> Unit,
) {
    val options = listOf(
        SelectionOption(
            value = DefaultLayoutMode.LAST_USED,
            label = stringResource(R.string.settings_default_layout_last_used),
            description = stringResource(R.string.settings_default_layout_last_used_desc),
        ),
        SelectionOption(
            value = DefaultLayoutMode.LATIN,
            label = stringResource(R.string.settings_default_layout_latin),
            description = stringResource(R.string.settings_default_layout_latin_desc),
        ),
        SelectionOption(
            value = DefaultLayoutMode.CYRILLIC,
            label = stringResource(R.string.settings_default_layout_cyrillic),
            description = stringResource(R.string.settings_default_layout_cyrillic_desc),
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_default_layout)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            RadioOptionList(
                options = options,
                selectedValue = selectedValue,
                onSelect = onSelect,
            )
        }
    }
}
