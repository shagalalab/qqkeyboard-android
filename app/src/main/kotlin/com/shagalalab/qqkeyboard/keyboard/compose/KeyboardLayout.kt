package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.ShiftState
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions

@Composable
fun KeyboardLayout(
    rows: List<List<KeyData>>,
    maxKeysInRow: Int,
    modifier: Modifier = Modifier,
    rowGap: Dp = KeyboardDimensions.rowGap,
    onKeyClick: (String) -> Unit,
    onKeyLongPress: ((String) -> Unit)? = null,
    onKeyRepeat: ((String) -> Unit)? = null,
    shiftState: ShiftState = ShiftState.OFF,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val standardKeyWidth = (maxWidth - KeyboardDimensions.gridHorizontalPadding * 2) / maxKeysInRow
        // The row gap is absorbed into each key's touch target rather than being dead space
        // between rows: each key extends rowGap/2 into the gap on the sides facing a neighbouring
        // row (none on the outer edges), so adjacent rows' touch targets are flush. The visual
        // gap is preserved via matching padding on the inner box in KeyButton.
        val halfGap = rowGap / 2
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = KeyboardDimensions.gridHorizontalPadding, vertical = KeyboardDimensions.gridVerticalPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            rows.forEachIndexed { index, keyRow ->
                KeyRow(
                    keys = keyRow,
                    standardKeyWidth = standardKeyWidth,
                    onKeyClick = onKeyClick,
                    onKeyLongPress = onKeyLongPress,
                    onKeyRepeat = onKeyRepeat,
                    shiftState = shiftState,
                    topTouchPadding = if (index == 0) 0.dp else halfGap,
                    bottomTouchPadding = if (index == rows.lastIndex) 0.dp else halfGap,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
