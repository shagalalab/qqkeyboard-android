package com.shagalalab.qqkeyboard.keyboard.compose

import android.content.res.Configuration
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shagalalab.qqkeyboard.keyboard.data.KeyboardMappings
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardLayout
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

            val isSpecialLayout = viewModel.isPasswordField || keyboardState.layout in setOf(
                KeyboardLayout.NUMBER_PAD, KeyboardLayout.NUMBER_PASSWORD, KeyboardLayout.PHONE
            )
            val showSuggestionStrip = !isSpecialLayout && viewModel.suggestionStripEnabled
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
                            isEmojiShown = keyboardState.isEmojiShown,
                            onSuggestionClick = viewModel::onSuggestionSelected,
                            onEmojiToggle = viewModel::toggleEmoji,
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

                if (keyboardState.isEmojiShown) {
                    EmojiLayout(viewModel::onKeyPressed, viewModel::toggleEmoji, viewModel.recentEmojis)
                }
            }

            Spacer(Modifier.height(bottomSystemBarsInset()))
        }
    }
}

/**
 * Height of the bottom system bars — in practice the navigation bar — read straight from the
 * window instead of through `WindowInsets.systemBars`.
 *
 * Compose's inset state only updates when the platform dispatches `onApplyWindowInsets` to the
 * ComposeView, and an IME window does not reliably receive that dispatch after a rotation: the
 * value measured in the previous orientation sticks. Landscape reports a much smaller bottom inset
 * than portrait (with gesture navigation the bar moves to the side and the bottom inset can be 0),
 * so rotating landscape → portrait left this spacer too short and the bottom key row ended up
 * behind the navigation bar, where it could not be tapped.
 *
 * Re-reading the root insets on every layout pass sidesteps the dispatch entirely. A rotation
 * always produces a layout pass, so the value always catches up.
 */
@Composable
private fun bottomSystemBarsInset(): Dp {
    val view = LocalView.current
    // Seeded during composition rather than from the effect below, so the first frame is already
    // spaced correctly instead of briefly rendering flush against the navigation bar.
    var insetPx by remember(view) { mutableIntStateOf(readBottomSystemBarsInset(view)) }

    DisposableEffect(view) {
        val observer = view.viewTreeObserver
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            insetPx = readBottomSystemBarsInset(view)
        }
        observer.addOnGlobalLayoutListener(listener)
        onDispose {
            if (observer.isAlive) observer.removeOnGlobalLayoutListener(listener)
            else view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    return with(LocalDensity.current) { insetPx.toDp() }
}

private fun readBottomSystemBarsInset(view: View): Int =
    ViewCompat.getRootWindowInsets(view)
        ?.getInsets(WindowInsetsCompat.Type.systemBars())
        ?.bottom ?: 0
