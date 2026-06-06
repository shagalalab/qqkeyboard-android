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
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardHeightScreen(
    selectedValue: KeyboardHeight,
    onSelect: (KeyboardHeight) -> Unit,
    onBackClick: () -> Unit,
) {
    val options = listOf(
        SelectionOption(KeyboardHeight.SHORT, stringResource(R.string.settings_height_short)),
        SelectionOption(KeyboardHeight.DEFAULT, stringResource(R.string.settings_height_default)),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_keyboard_height)) },
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
