package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.layout.Arrangement
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
    modifier: Modifier = Modifier,
    onKeyClick: (String) -> Unit,
    onKeyLongPress: ((String) -> Unit)? = null,
    isShiftActive: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        rows.forEachIndexed { index, keyRow ->
            KeyRow(
                keys = keyRow,
                onKeyClick = onKeyClick,
                onKeyLongPress = onKeyLongPress,
                isShiftActive = isShiftActive,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
