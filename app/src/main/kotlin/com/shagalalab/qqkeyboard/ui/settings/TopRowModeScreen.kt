package com.shagalalab.qqkeyboard.ui.settings

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
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopRowModeScreen(
    selectedValue: TopRowMode,
    onSelect: (TopRowMode) -> Unit,
    onBackClick: () -> Unit,
) {
    val options = listOf(
        SelectionOption(
            value = TopRowMode.EXTRA_LETTERS,
            label = stringResource(R.string.settings_top_row_extra_letters),
            description = stringResource(R.string.settings_top_row_extra_letters_desc),
        ),
        SelectionOption(
            value = TopRowMode.NUMBERS,
            label = stringResource(R.string.settings_top_row_numbers),
            description = stringResource(R.string.settings_top_row_numbers_desc),
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_top_row_layout)) },
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
        RadioOptionList(
            options = options,
            selectedValue = selectedValue,
            onSelect = onSelect,
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        )
    }
}
