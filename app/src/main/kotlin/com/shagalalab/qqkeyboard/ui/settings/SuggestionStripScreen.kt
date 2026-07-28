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
import com.shagalalab.qqkeyboard.keyboard.compose.SuggestionStrip
import com.shagalalab.qqkeyboard.keyboard.data.KeyboardMappings
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardThemes
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardBorderEnabled
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardHeight

// Stand-ins for real suggestions, so the preview shows what the strip looks like in use.
private val PREVIEW_SUGGESTIONS = listOf("men", "sálem", "sen")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionStripScreen(
    selectedValue: Boolean,
    topRowMode: TopRowMode,
    onSelect: (Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    val options = listOf(
        SelectionOption(
            value = true,
            label = stringResource(R.string.settings_suggestion_strip_on),
        ),
        SelectionOption(
            value = false,
            label = stringResource(R.string.settings_suggestion_strip_off),
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_suggestion_strip)) },
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
            SuggestionStripPreview(
                showStrip = selectedValue,
                topRowMode = topRowMode,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun SuggestionStripPreview(
    showStrip: Boolean,
    topRowMode: TopRowMode,
    modifier: Modifier = Modifier,
) {
    val colors = KeyboardThemes.SystemAuto.resolvedColors(isSystemInDarkTheme())
    val previewRows = KeyboardMappings.getLatinLayout(topRowMode).take(1)

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
                Column {
                    if (showStrip) {
                        SuggestionStrip(
                            suggestions = PREVIEW_SUGGESTIONS,
                            isEmojiShown = false,
                            onSuggestionClick = {},
                            onEmojiToggle = {},
                        )
                    }
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
}
