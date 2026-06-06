package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.compose.KeyboardLayout
import com.shagalalab.qqkeyboard.keyboard.data.KeyboardMappings
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardThemes
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardBorderEnabled
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardHeight

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
            TopRowPreview(
                selectedMode = selectedValue,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun TopRowPreview(
    selectedMode: TopRowMode,
    modifier: Modifier = Modifier,
) {
    val colors = KeyboardThemes.SystemAuto.resolvedColors(isSystemInDarkTheme())
    val previewRows = KeyboardMappings.getLatinLayout(selectedMode).take(2)

    CompositionLocalProvider(
        LocalKeyboardColors provides colors,
        LocalKeyboardHeight provides KeyboardHeight.DEFAULT,
        LocalKeyboardBorderEnabled provides true,
    ) {
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.settings_preview),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Surface(
                color = colors.keyboardBackground,
                shape = RoundedCornerShape(12.dp),
            ) {
                KeyboardLayout(
                    rows = previewRows,
                    maxKeysInRow = 10,
                    onKeyClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
