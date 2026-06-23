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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.ShiftState
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import com.shagalalab.qqkeyboard.keyboard.utils.kaaUppercase
import com.shagalalab.qqkeyboard.keyboard.utils.kaaUppercaseChar

private const val MAX_SUGGESTIONS_TO_SHOW = 3

@Composable
fun SuggestionStrip(
    suggestions: List<String>,
    isEmojiShown: Boolean,
    onSuggestionClick: (String) -> Unit,
    onEmojiToggle: () -> Unit,
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
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isEmojiShown) {
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

        Box(
            modifier = Modifier
                .background(colors.modifierBackground, CircleShape)
                .clickable { onEmojiToggle() }
                .padding(KeyboardDimensions.emojiTogglePadding),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_smile),
                contentDescription = null,
                tint = colors.keyContent,
                modifier = Modifier.size(KeyboardDimensions.emojiToggleIconSize),
            )
        }
    }
}
