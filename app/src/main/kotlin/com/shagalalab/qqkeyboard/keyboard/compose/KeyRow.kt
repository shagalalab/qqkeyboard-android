package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.KeyType
import com.shagalalab.qqkeyboard.keyboard.model.ShiftState

// A row with fewer keys than the grid holds is centred, leaving a gap at both ends. That gap
// belongs to the edge keys' touch area — a tap left of "a" or right of "l" still types them —
// while the keys keep their drawn size. Rows with a much wider gap (the six-letter top row) only
// hand over half a key on each side, so taps far from any key stay dead.
private const val MAX_EDGE_TOUCH_EXPANSION = 0.5f

@Composable
fun KeyRow(
    keys: List<KeyData>,
    standardKeyWidth: Dp,
    maxKeysInRow: Int,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onKeyLongPress: ((String) -> Unit)? = null,
    onKeyRepeat: ((String) -> Unit)? = null,
    onAlternateHighlight: (() -> Unit)? = null,
    shiftState: ShiftState = ShiftState.OFF,
    topTouchPadding: Dp = 0.dp,
    bottomTouchPadding: Dp = 0.dp,
) {
    val hasSpaceKey = keys.any { it.keyType == KeyType.SPACE }

    // Rows with a space bar or a filling key already stretch to both edges, so they have no gap.
    val edgeTouchPadding = if (hasSpaceKey || keys.any { it.fillSpace }) {
        0.dp
    } else {
        val slack = maxKeysInRow - keys.fold(0f) { total, key -> total + key.widthRatio }
        standardKeyWidth * (slack / 2f).coerceIn(0f, MAX_EDGE_TOUCH_EXPANSION)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (hasSpaceKey) {
            Arrangement.spacedBy(0.dp)
        } else {
            Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally)
        }
    ) {
        keys.forEachIndexed { index, keyData ->
            val startTouchPadding = if (index == 0) edgeTouchPadding else 0.dp
            val endTouchPadding = if (index == keys.lastIndex) edgeTouchPadding else 0.dp
            KeyButton(
                keyData = keyData,
                onKeyClick = onKeyClick,
                onKeyRepeat = onKeyRepeat,
                onKeyLongPress = when {
                    onKeyLongPress == null -> null
                    keyData.code == "SHIFT" || keyData.code == "BACKSPACE" || keyData.code == "SPACE" -> onKeyLongPress
                    keyData.alternativeChars.isNotEmpty() -> onKeyLongPress
                    else -> null
                },
                onAlternateHighlight = onAlternateHighlight,
                shiftState = shiftState,
                topTouchPadding = topTouchPadding,
                bottomTouchPadding = bottomTouchPadding,
                startTouchPadding = startTouchPadding,
                endTouchPadding = endTouchPadding,
                modifier = when {
                    hasSpaceKey -> Modifier.weight(keyData.widthRatio)
                    keyData.fillSpace -> Modifier.weight(1f)
                    else -> Modifier.width(standardKeyWidth * keyData.widthRatio + startTouchPadding + endTouchPadding)
                }
            )
        }
    }
}
