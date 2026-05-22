package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shagalalab.qqkeyboard.keyboard.model.ShiftState
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors

private val STRIP_HEIGHT = 40.dp

@Composable
fun SuggestionStrip(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    shiftState: ShiftState = ShiftState.OFF,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKeyboardColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(STRIP_HEIGHT)
            .background(colors.keyboardBackground),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        suggestions.take(3).forEachIndexed { index, suggestion ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(0.5f)
                        .background(colors.modifierBackground)
                )
            }
            val displayText = when (shiftState) {
                ShiftState.CAPS_LOCK -> suggestion.uppercase()
                ShiftState.ON -> suggestion.replaceFirstChar { it.uppercaseChar() }
                ShiftState.OFF -> suggestion
            }
            Text(
                text = displayText,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSuggestionClick(suggestion) }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                color = colors.keyContent,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
