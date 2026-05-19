package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.keyboard.model.KeyData

@Composable
fun KeyboardLayout(
    rows: List<List<KeyData>>,
    maxKeysInRow: Int,
    modifier: Modifier = Modifier,
    onKeyClick: (String) -> Unit,
    onKeyLongPress: ((String) -> Unit)? = null,
    onKeyRepeat: ((String) -> Unit)? = null,
    isShiftActive: Boolean = false,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // 4.dp = 2.dp left + 2.dp right from Column padding
        // (maxKeysInRow - 1) * 2.dp = gaps between keys in the widest row
        // Gaps between keys are now handled as inner visual padding in KeyButton (1dp each side),
        // so no gap space is deducted here — touch areas tile flush across the full row width.
        val standardKeyWidth = (maxWidth - 4.dp) / maxKeysInRow
        Column(
            modifier = Modifier.fillMaxWidth().padding(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            rows.forEach { keyRow ->
                KeyRow(
                    keys = keyRow,
                    standardKeyWidth = standardKeyWidth,
                    onKeyClick = onKeyClick,
                    onKeyLongPress = onKeyLongPress,
                    onKeyRepeat = onKeyRepeat,
                    isShiftActive = isShiftActive,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
