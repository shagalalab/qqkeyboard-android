package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.KeyType
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import com.shagalalab.qqkeyboard.keyboard.utils.kaaUppercase
import kotlinx.coroutines.delay

val KEY_HEIGHT = 48.dp
private const val REPEAT_INTERVAL_DELAY_MS = 50L
private const val BUBBLE_DISMISS_MS = 500L
private val BUBBLE_SHAPE = RoundedCornerShape(8.dp)

@OptIn(ExperimentalFoundationApi::class)
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
    var isLongPressing by remember { mutableStateOf(false) }
    var showBubble by remember { mutableStateOf(false) }

    val keyShape = RoundedCornerShape(6.dp)
    val colors = LocalKeyboardColors.current

    val (backgroundColor, borderColor, contentColor) = when {
        keyData.code == "SPACER" -> Triple(
            Color.Transparent,
            Color.Transparent,
            Color.Transparent
        )
        isPressed -> Triple(
            colors.pressedBackground,
            colors.pressedBorder,
            colors.keyContent
        )
        keyData.keyType == KeyType.MODIFIER && keyData.code == "SHIFT" && isShiftActive -> Triple(
            colors.shiftActiveBackground,
            colors.shiftActiveBackground,
            colors.shiftActiveContent
        )
        keyData.keyType == KeyType.MODIFIER || keyData.keyType == KeyType.ACTION -> Triple(
            colors.modifierBackground,
            colors.modifierBorder,
            colors.modifierContent
        )
        else -> Triple(
            colors.keyBackground,
            colors.keyBorder,
            colors.keyContent
        )
    }

    // Handle repetitive long press for backspace
    LaunchedEffect(isLongPressing) {
        if (isLongPressing && (keyData.code == "BACKSPACE")) {
            while (isLongPressing) {
                onKeyClick(keyData.code)
                delay(REPEAT_INTERVAL_DELAY_MS)
            }
        }
    }

    // Stop long pressing when the key is released
    LaunchedEffect(isPressed) {
        if (!isPressed && isLongPressing) {
            isLongPressing = false
        }
    }

    // Auto-dismiss bubble after a short delay
    LaunchedEffect(showBubble) {
        if (showBubble) {
            delay(BUBBLE_DISMISS_MS)
            showBubble = false
        }
    }

    // Outer box: full allocated width — this is the touch target.
    // Inner box: inset by 1dp on each side — this is what the user sees.
    // Adjacent keys' outer boxes are flush (0dp gap), so there is no dead zone between keys,
    // while the visual 2dp gap is preserved via the 1dp insets on each side.
    Box(
        modifier = modifier
            .height(KEY_HEIGHT)
            .let { m ->
                if (keyData.code != "SPACER") {
                    m.combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onKeyClick(keyData.code) },
                        onLongClick = {
                            if (keyData.code == "BACKSPACE") {
                                isLongPressing = true
                            } else {
                                if (keyData.secondaryLabel != null) showBubble = true
                                onKeyLongPress?.invoke()
                            }
                        }
                    )
                } else m
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 1.dp)
                .clip(keyShape)
                .background(backgroundColor)
                .border(width = 1.dp, color = borderColor, shape = keyShape),
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
                keyData.hintText != null -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = keyData.displayText,
                            color = contentColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                        Text(
                            text = keyData.hintText,
                            modifier = Modifier.weight(1f),
                            color = contentColor.copy(alpha = 0.55f),
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp,
                            maxLines = 1
                        )
                    }
                }
                keyData.displayText.isNotEmpty() -> {
                    Text(
                        text = if (isShiftActive && keyData.keyType == KeyType.CHARACTER) {
                            keyData.displayText.kaaUppercase()
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
            if (keyData.secondaryLabel != null) {
                Text(
                    text = keyData.secondaryLabel,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 3.dp, top = 2.dp),
                    color = contentColor.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }
        }

        // Long-press bubble: floats above the key, drawn over the row above via
        // negative offset. Compose Column paints rows in order so this row draws
        // on top of the previous row — no Popup/separate window needed.
        if (showBubble && keyData.secondaryLabel != null) {
            val label = if (isShiftActive) keyData.secondaryLabel.kaaUppercase() else keyData.secondaryLabel
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = -(KEY_HEIGHT + 6.dp))
                    .zIndex(1f)
                    .defaultMinSize(minWidth = KEY_HEIGHT)
                    .height(KEY_HEIGHT)
                    .shadow(6.dp, BUBBLE_SHAPE)
                    .background(colors.bubbleBackground, BUBBLE_SHAPE)
                    .border(1.dp, colors.keyBorder, BUBBLE_SHAPE)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = colors.keyContent,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}
