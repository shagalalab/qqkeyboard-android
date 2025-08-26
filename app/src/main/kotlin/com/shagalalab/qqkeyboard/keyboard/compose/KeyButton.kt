package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.KeyType

@Composable
fun KeyButton(
    keyData: KeyData,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onKeyLongPress: (() -> Unit)? = null,
    isShiftActive: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val keyShape = RoundedCornerShape(6.dp)

    // Key colors based on type
    val (backgroundColor, borderColor, contentColor) = when {
        isPressed -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onSurface
        )
        keyData.keyType == KeyType.MODIFIER && keyData.code == "SHIFT" && isShiftActive -> Triple(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        keyData.keyType == KeyType.MODIFIER || keyData.keyType == KeyType.ACTION -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.outline,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        else -> Triple(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.outline,
            MaterialTheme.colorScheme.onSurface
        )
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(keyShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = keyShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onKeyClick(keyData.code)
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            keyData.iconResId != null -> {
                Icon(
                    painter = painterResource(keyData.iconResId),
                    contentDescription = keyData.code,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            keyData.displayText.isNotEmpty() -> {
                Text(
                    text = if (isShiftActive && keyData.keyType == KeyType.CHARACTER) {
                        keyData.displayText.uppercase()
                    } else {
                        keyData.displayText
                    },
                    color = contentColor,
                    fontSize = when (keyData.keyType) {
                        KeyType.CHARACTER -> 22.sp
                        KeyType.LAYOUT_SWITCH -> 16.sp
                        else -> 14.sp
                    },
                    fontWeight = when (keyData.keyType) {
                        KeyType.MODIFIER, KeyType.ACTION -> FontWeight.Medium
                        else -> FontWeight.Normal
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }

    // Long press handling will be implemented later if needed
}
