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

@Composable
fun KeyRow(
    keys: List<KeyData>,
    standardKeyWidth: Dp,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onKeyLongPress: ((String) -> Unit)? = null,
    onKeyRepeat: ((String) -> Unit)? = null,
    isShiftActive: Boolean = false,
) {
    // Rows containing a space key fill the full width with weights (space expands to fill).
    // All other rows use fixed key widths and are centered, so shorter rows don't stretch keys.
    val hasSpaceKey = keys.any { it.keyType == KeyType.SPACE }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (hasSpaceKey) {
            Arrangement.spacedBy(0.dp)
        } else {
            Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally)
        }
    ) {
        keys.forEach { keyData ->
            KeyButton(
                keyData = keyData,
                onKeyClick = onKeyClick,
                onKeyRepeat = onKeyRepeat,
                onKeyLongPress = if (onKeyLongPress != null) {
                    when {
                        keyData.code == "SHIFT" || keyData.code == "BACKSPACE" ->
                            { { onKeyLongPress(keyData.code) } }
                        keyData.longPressCode != null ->
                            { { onKeyLongPress(keyData.longPressCode) } }
                        else -> null
                    }
                } else null,
                isShiftActive = isShiftActive,
                modifier = when {
                    hasSpaceKey -> Modifier.weight(keyData.widthRatio)
                    keyData.fillRight -> Modifier.weight(1f)
                    else -> Modifier.width(standardKeyWidth * keyData.widthRatio)
                }
            )
        }
    }
}
