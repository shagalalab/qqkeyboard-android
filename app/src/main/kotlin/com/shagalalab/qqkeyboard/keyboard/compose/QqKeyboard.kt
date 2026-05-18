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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.keyboard.data.KeyboardMappings
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardLayout
import com.shagalalab.qqkeyboard.keyboard.viewmodel.KeyboardViewModel

@Composable
fun QqKeyboard(
    viewModel: KeyboardViewModel,
    modifier: Modifier = Modifier
) {
    val keyboardState = viewModel.keyboardState
    val currentImeAction = viewModel.currentImeAction

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(KEY_HEIGHT * 5 + 2.dp * 7)
                .padding(2.dp)
        ) {
            val keyboardLayout: List<List<KeyData>> = when (keyboardState.layout) {
                KeyboardLayout.LATIN -> KeyboardMappings.getLatinLayout(currentImeAction)
                KeyboardLayout.CYRILLIC -> KeyboardMappings.getCyrillicLayout(currentImeAction)
                KeyboardLayout.NUMERIC -> KeyboardMappings.getNumericLayout(currentImeAction)
                KeyboardLayout.SYMBOLIC -> KeyboardMappings.getSymbolicLayout(currentImeAction)
            }

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

            val maxKeysInRow = if (keyboardState.layout == KeyboardLayout.CYRILLIC) 11 else 10

            KeyboardLayout(
                rows = updatedLayout,
                maxKeysInRow = maxKeysInRow,
                modifier = Modifier.fillMaxWidth(),
                onKeyClick = { key -> viewModel.onKeyPressed(key) },
                onKeyLongPress = { key ->
                    when (key) {
                        "SHIFT" -> viewModel.onShiftLongPress()
                        "BACKSPACE" -> viewModel.onBackspaceLongPress()
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
}
