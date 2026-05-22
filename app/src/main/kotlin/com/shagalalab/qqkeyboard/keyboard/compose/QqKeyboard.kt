package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.keyboard.data.KeyboardMappings
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardLayout
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardBorderEnabled
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.theme.toDp
import com.shagalalab.qqkeyboard.keyboard.viewmodel.KeyboardViewModel

@Composable
fun QqKeyboard(
    viewModel: KeyboardViewModel,
    modifier: Modifier = Modifier
) {
    val keyboardState = viewModel.keyboardState
    val currentImeAction = viewModel.currentImeAction

    CompositionLocalProvider(
        LocalKeyboardColors provides viewModel.currentTheme.colors,
        LocalKeyboardHeight provides viewModel.keyboardHeight,
        LocalKeyboardBorderEnabled provides viewModel.keyBorderEnabled,
    ) {
    val colors = LocalKeyboardColors.current
    val keyHeight = LocalKeyboardHeight.current.toDp()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.keyboardBackground)
    ) {
        if ((keyboardState.layout == KeyboardLayout.LATIN || keyboardState.layout == KeyboardLayout.CYRILLIC) && !keyboardState.isEmojiShown) {
            SuggestionStrip(
                suggestions = viewModel.suggestions,
                onSuggestionClick = viewModel::onSuggestionSelected,
            )
        }

        val topRowMode = viewModel.topRowMode
        val keyboardLayout: List<List<KeyData>> = when (keyboardState.layout) {
            KeyboardLayout.LATIN -> KeyboardMappings.getLatinLayout(topRowMode, currentImeAction)
            KeyboardLayout.CYRILLIC -> KeyboardMappings.getCyrillicLayout(topRowMode, currentImeAction)
            KeyboardLayout.NUMERIC -> KeyboardMappings.getNumericLayout(currentImeAction)
            KeyboardLayout.SYMBOLIC -> KeyboardMappings.getSymbolicLayout(currentImeAction)
            KeyboardLayout.NUMBER_PAD -> KeyboardMappings.getNumberPadLayout(currentImeAction)
            KeyboardLayout.NUMBER_PASSWORD -> KeyboardMappings.getNumberPasswordLayout(currentImeAction)
            KeyboardLayout.PHONE -> KeyboardMappings.getPhoneLayout(currentImeAction)
        }

        val numRows = keyboardLayout.size
        val maxKeysInRow = when (keyboardState.layout) {
            KeyboardLayout.CYRILLIC -> 11
            KeyboardLayout.NUMBER_PAD, KeyboardLayout.NUMBER_PASSWORD, KeyboardLayout.PHONE -> 4
            else -> 10
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(keyHeight * numRows + 2.dp * (numRows + 2))
                .padding(2.dp)
        ) {
            val updatedLayout: List<List<KeyData>> = keyboardLayout.map { row ->
                row.map { keyData ->
                    when (keyData.code) {
                        "LAYOUT_SWITCH" -> keyData.copy(
                            displayText = viewModel.getLayoutSwitchButtonText()
                        )

                        "123", "ABC", "€~\\" -> keyData.copy(
                            displayText = keyData.code
                        )

                        else -> keyData
                    }
                }
            }

            KeyboardLayout(
                rows = updatedLayout,
                maxKeysInRow = maxKeysInRow,
                modifier = Modifier.fillMaxWidth(),
                onKeyClick = { key -> viewModel.onKeyPressed(key) },
                onKeyRepeat = { viewModel.onBackspaceRepeat() },
                onKeyLongPress = { key ->
                    when (key) {
                        "SHIFT" -> viewModel.onShiftLongPress()
                        "BACKSPACE" -> viewModel.onBackspaceLongPress()
                        else -> viewModel.onKeyPressed(key)
                    }
                },
                isShiftActive = keyboardState.shouldShowUpperCase,
            )

            if (keyboardState.isEmojiShown) {
                EmojiLayout(viewModel::onKeyPressed, viewModel::toggleEmoji, viewModel.recentEmojis)
            }
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
    } // CompositionLocalProvider
}
