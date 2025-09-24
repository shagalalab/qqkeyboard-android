package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.keyboard.data.KeyboardMappings
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardMode
import com.shagalalab.qqkeyboard.keyboard.model.LayoutType
import com.shagalalab.qqkeyboard.keyboard.viewmodel.KeyboardViewModel

@Composable
fun QqKeyboard(
    viewModel: KeyboardViewModel,
    modifier: Modifier = Modifier
) {
    val keyboardState = viewModel.keyboardState
    val currentImeAction = viewModel.currentImeAction

    Box(modifier.fillMaxWidth()
        .height(KEY_HEIGHT * 5 + 2.dp * 7)
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(2.dp)
    ) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // Get the appropriate layout based on current state
            val keyboardLayout: List<List<KeyData>> = when {
                keyboardState.keyboardMode == KeyboardMode.NUMERIC -> {
                    KeyboardMappings.getNumericLayout(currentImeAction)
                }

                keyboardState.keyboardMode == KeyboardMode.SYMBOLIC -> {
                    KeyboardMappings.getSymbolicLayout(currentImeAction)
                }

                keyboardState.layoutType == LayoutType.LATIN -> {
                    KeyboardMappings.getLatinLayout(currentImeAction)
                }

                keyboardState.layoutType == LayoutType.CYRILLIC -> {
                    KeyboardMappings.getCyrillicLayout(currentImeAction)
                }

                else -> KeyboardMappings.getLatinLayout(currentImeAction)
            }

            // Update layout switch button text dynamically
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
                onKeyClick = { key -> viewModel.onKeyPressed(key) },
                onKeyLongPress = { key ->
                    when (key) {
                        "SHIFT" -> viewModel.onShiftLongPress()
                        "BACKSPACE" -> viewModel.onBackspaceLongPress()
                    }
                },
                isShiftActive = keyboardState.shouldShowUpperCase,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (keyboardState.isEmojiShown) {
            EmojiLayout(viewModel::onKeyPressed, viewModel::toggleEmoji, viewModel.recentEmojis)
        }
    }
}
