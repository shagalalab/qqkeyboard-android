package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.ShiftState
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions

@Composable
fun KeyboardLayout(
    rows: List<List<KeyData>>,
    maxKeysInRow: Int,
    modifier: Modifier = Modifier,
    onKeyClick: (String) -> Unit,
    onKeyLongPress: ((String) -> Unit)? = null,
    onKeyRepeat: ((String) -> Unit)? = null,
    shiftState: ShiftState = ShiftState.OFF,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val standardKeyWidth = (maxWidth - KeyboardDimensions.gridPadding * 2) / maxKeysInRow
        Column(
            modifier = Modifier.fillMaxWidth().padding(KeyboardDimensions.gridPadding),
            verticalArrangement = Arrangement.spacedBy(KeyboardDimensions.rowGap)
        ) {
            rows.forEach { keyRow ->
                KeyRow(
                    keys = keyRow,
                    standardKeyWidth = standardKeyWidth,
                    onKeyClick = onKeyClick,
                    onKeyLongPress = onKeyLongPress,
                    onKeyRepeat = onKeyRepeat,
                    shiftState = shiftState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
