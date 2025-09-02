package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.keyboard.model.KeyData

@Composable
fun KeyRow(
    keys: List<KeyData>,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onKeyLongPress: ((String) -> Unit)? = null,
    isShiftActive: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        keys.forEach { keyData ->
            KeyButton(
                keyData = keyData,
                onKeyClick = onKeyClick,
                onKeyLongPress = if (onKeyLongPress != null && (keyData.code == "SHIFT" || keyData.code == "BACKSPACE")) {
                    { onKeyLongPress(keyData.code) }
                } else null,
                isShiftActive = isShiftActive,
                modifier = Modifier.weight(keyData.widthRatio)
            )
        }
    }
}
