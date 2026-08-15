package com.shagalalab.qqkeyboard.keyboard.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.keyboard.data.KeyboardMappings
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardLayout
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardPanel
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions
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

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val effectiveKeyboardHeight = if (isLandscape) KeyboardHeight.LANDSCAPE else viewModel.keyboardHeight
    val effectiveRowGap = if (isLandscape) KeyboardDimensions.rowGapLandscape else KeyboardDimensions.rowGap

    CompositionLocalProvider(
        LocalKeyboardColors provides viewModel.currentTheme.resolvedColors(isSystemInDarkTheme()),
        LocalKeyboardHeight provides effectiveKeyboardHeight,
        LocalKeyboardBorderEnabled provides viewModel.keyBorderEnabled,
    ) {
        val colors = LocalKeyboardColors.current
        val keyHeight = LocalKeyboardHeight.current.toDp()

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(colors.keyboardBackground)
        ) {
            val topRowMode = viewModel.topRowMode
            val switchButtonText = viewModel.getLayoutSwitchButtonText()
            val bottomRowCommaKey = viewModel.bottomRowCommaKey

            val showSuggestionStrip = !viewModel.isSpecialLayout && viewModel.suggestionStripEnabled
            // Whenever the strip isn't drawn its emoji button goes with it, so the letter
            // layouts' bottom row takes over as the way into the emoji picker.
            val showEmojiKey = !showSuggestionStrip

            val updatedLayout = remember(keyboardState.layout, topRowMode, currentImeAction, switchButtonText, bottomRowCommaKey, showEmojiKey) {
                val baseLayout = when (keyboardState.layout) {
                    KeyboardLayout.LATIN -> KeyboardMappings.getLatinLayout(topRowMode, currentImeAction, showEmojiKey)
                    KeyboardLayout.CYRILLIC -> KeyboardMappings.getCyrillicLayout(topRowMode, currentImeAction, showEmojiKey)
                    KeyboardLayout.NUMERIC -> KeyboardMappings.getNumericLayout(currentImeAction)
                    KeyboardLayout.SYMBOLIC -> KeyboardMappings.getSymbolicLayout(currentImeAction)
                    KeyboardLayout.NUMBER_PAD -> KeyboardMappings.getNumberPadLayout(currentImeAction)
                    KeyboardLayout.NUMBER_PASSWORD -> KeyboardMappings.getNumberPasswordLayout(currentImeAction)
                    KeyboardLayout.PHONE -> KeyboardMappings.getPhoneLayout(currentImeAction)
                }
                baseLayout.map { row ->
                    row.map { keyData ->
                        when (keyData.code) {
                            "LAYOUT_SWITCH" -> keyData.copy(displayText = switchButtonText)
                            "123", "ABC", "€~\\" -> keyData.copy(displayText = keyData.code)
                            "," -> keyData.copy(code = bottomRowCommaKey, displayText = bottomRowCommaKey)
                            else -> keyData
                        }
                    }
                }
            }

            val numRows = updatedLayout.size
            val maxKeysInRow = when (keyboardState.layout) {
                KeyboardLayout.CYRILLIC -> 11
                KeyboardLayout.NUMBER_PAD, KeyboardLayout.NUMBER_PASSWORD, KeyboardLayout.PHONE -> 4
                else -> 10
            }

            val keyAreaHeight = keyHeight * numRows + effectiveRowGap * (numRows - 1) + KeyboardDimensions.gridVerticalPadding * 2
            val totalHeight = keyAreaHeight + if (showSuggestionStrip) KeyboardDimensions.suggestionStripHeight else 0.dp

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(totalHeight)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    if (showSuggestionStrip) {
                        SuggestionStrip(
                            suggestions = viewModel.suggestions,
                            activePanel = keyboardState.panel,
                            onSuggestionClick = viewModel::onSuggestionSelected,
                            onEmojiToggle = viewModel::toggleEmoji,
                            onClipboardToggle = viewModel::toggleClipboard,
                            shiftState = viewModel.suggestionShiftState,
                        )
                    }

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(keyAreaHeight)
                            .padding(horizontal = KeyboardDimensions.gridHorizontalPadding)
                    ) {
                        KeyboardLayout(
                            rows = updatedLayout,
                            maxKeysInRow = maxKeysInRow,
                            modifier = Modifier.fillMaxWidth(),
                            rowGap = effectiveRowGap,
                            onKeyClick = { key -> viewModel.onKeyPressed(key) },
                            onKeyRepeat = { viewModel.onBackspaceRepeat() },
                            onKeyLongPress = { key ->
                                when (key) {
                                    "SHIFT" -> viewModel.onShiftLongPress()
                                    "BACKSPACE" -> viewModel.onBackspaceLongPress()
                                    "SPACE" -> viewModel.onSpaceLongPress()
                                    else -> viewModel.onKeyPressed(key)
                                }
                            },
                            onAlternateHighlight = viewModel::onAlternateHighlight,
                            shiftState = keyboardState.shiftState,
                        )
                    }
                }

                when (keyboardState.panel) {
                    KeyboardPanel.EMOJI ->
                        EmojiLayout(viewModel::onKeyPressed, viewModel::toggleEmoji, viewModel.recentEmojis)
                    KeyboardPanel.CLIPBOARD ->
                        ClipboardLayout(
                            clips = viewModel.clips,
                            onClipClick = viewModel::onClipSelected,
                            onClose = viewModel::toggleClipboard,
                        )
                    KeyboardPanel.NONE -> {}
                }
            }

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}
