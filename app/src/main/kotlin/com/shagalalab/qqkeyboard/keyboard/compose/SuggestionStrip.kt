package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardPanel
import com.shagalalab.qqkeyboard.keyboard.model.ShiftState
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import com.shagalalab.qqkeyboard.keyboard.utils.kaaUppercase
import com.shagalalab.qqkeyboard.keyboard.utils.kaaUppercaseChar

private const val MAX_SUGGESTIONS_TO_SHOW = 3

@Composable
fun SuggestionStrip(
    suggestions: List<String>,
    activePanel: KeyboardPanel,
    onSuggestionClick: (String) -> Unit,
    onEmojiToggle: () -> Unit,
    onClipboardToggle: () -> Unit,
    modifier: Modifier = Modifier,
    shiftState: ShiftState = ShiftState.OFF,
) {
    val colors = LocalKeyboardColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(KeyboardDimensions.suggestionStripHeight)
            .padding(horizontal = KeyboardDimensions.suggestionStripHorizontalPadding)
            .background(colors.keyboardBackground),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StripIconButton(
            iconResId = R.drawable.ic_clipboard,
            contentDescription = stringResource(R.string.cd_clipboard),
            onClick = onClipboardToggle,
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // An open panel is drawn over the strip, so this is not about what is visible: it keeps
            // covered suggestions from lingering in the semantics tree for screen readers.
            if (activePanel == KeyboardPanel.NONE) {
                suggestions.take(MAX_SUGGESTIONS_TO_SHOW).forEach { suggestion ->
                    val displayText = when (shiftState) {
                        ShiftState.CAPS_LOCK -> suggestion.kaaUppercase()
                        ShiftState.ON -> suggestion.replaceFirstChar { it.kaaUppercaseChar() }
                        ShiftState.OFF -> suggestion
                    }
                    Text(
                        text = displayText,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSuggestionClick(displayText) }
                            .padding(horizontal = KeyboardDimensions.suggestionPaddingHorizontal, vertical = KeyboardDimensions.suggestionPaddingVertical),
                        color = colors.keyContent,
                        fontSize = KeyboardDimensions.suggestionFontSize,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        StripIconButton(
            iconResId = R.drawable.ic_smile,
            contentDescription = null,
            onClick = onEmojiToggle,
        )
    }
}

/**
 * One of the two round buttons bookending the strip — clipboard at the start, emoji at the end.
 *
 * Neither shows an open/closed state: a panel covers the strip completely, so there would be
 * nothing to see it on.
 */
@Composable
private fun StripIconButton(
    iconResId: Int,
    contentDescription: String?,
    onClick: () -> Unit,
) {
    val colors = LocalKeyboardColors.current
    Box(
        modifier = Modifier
            .background(colors.modifierBackground, CircleShape)
            .clickable(onClick = onClick)
            .padding(KeyboardDimensions.stripIconPadding),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = contentDescription,
            tint = colors.keyContent,
            modifier = Modifier.size(KeyboardDimensions.stripIconSize),
        )
    }
}
