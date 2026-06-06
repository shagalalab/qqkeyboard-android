package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.shagalalab.qqkeyboard.keyboard.compose.KeyButton
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardThemes
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardBorderEnabled
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardHeight

private val PREVIEW_KEYS = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")

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
            KeyHeightPreview(
                selectedHeight = selectedValue,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun KeyHeightPreview(
    selectedHeight: KeyboardHeight,
    modifier: Modifier = Modifier,
) {
    val colors = KeyboardThemes.SystemAuto.resolvedColors(isSystemInDarkTheme())

    CompositionLocalProvider(
        LocalKeyboardColors provides colors,
        LocalKeyboardHeight provides selectedHeight,
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(KeyboardDimensions.gridPadding),
                ) {
                    PREVIEW_KEYS.forEach { key ->
                        KeyButton(
                            keyData = KeyData.character(key),
                            onKeyClick = {},
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
