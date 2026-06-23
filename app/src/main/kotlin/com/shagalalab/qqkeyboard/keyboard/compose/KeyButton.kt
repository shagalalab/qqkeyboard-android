package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.KeyType
import com.shagalalab.qqkeyboard.keyboard.model.ShiftState
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardBorderEnabled
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.theme.toDp
import com.shagalalab.qqkeyboard.keyboard.utils.kaaUppercase
import kotlinx.coroutines.delay

private const val REPEAT_INTERVAL_DELAY_MS = 50L
private const val BUBBLE_DISMISS_MS = 500L
private val BUBBLE_SHAPE = RoundedCornerShape(KeyboardDimensions.bubbleCornerRadius)
private val KEY_SHAPE = RoundedCornerShape(KeyboardDimensions.keyCornerRadius)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyButton(
    keyData: KeyData,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onKeyLongPress: (() -> Unit)? = null,
    onKeyRepeat: ((String) -> Unit)? = null,
    shiftState: ShiftState = ShiftState.OFF
) {
    val isShiftActive = shiftState != ShiftState.OFF
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isLongPressing by remember { mutableStateOf(false) }
    var showBubble by remember { mutableStateOf(false) }
    var bubbleLabel by remember { mutableStateOf("") }

    val colors = LocalKeyboardColors.current
    val keyHeight = LocalKeyboardHeight.current.toDp()
    val keyBackgroundEnabled = LocalKeyboardBorderEnabled.current

    val (backgroundColor, contentColor) = when {
        keyData.code == "SPACER" -> Pair(Color.Transparent, Color.Transparent)
        isPressed -> Pair(if (keyBackgroundEnabled) colors.keyboardBackground else colors.keyBackground, colors.keyContent)
        !keyBackgroundEnabled -> Pair(Color.Transparent, colors.keyContent)
        keyData.keyType == KeyType.MODIFIER || keyData.keyType == KeyType.ACTION -> Pair(colors.modifierBackground, colors.modifierContent)
        else -> Pair(colors.keyBackground, colors.keyContent)
    }

    val effectiveIconResId = when {
        keyData.code == "SHIFT" -> when (shiftState) {
            ShiftState.OFF -> R.drawable.shift_off
            ShiftState.ON -> R.drawable.shift_on
            ShiftState.CAPS_LOCK -> R.drawable.caps_on
        }
        else -> keyData.iconResId
    }

    // Handle repetitive long press for backspace — uses onKeyRepeat (no feedback)
    // so only the initial onClick vibration fires, matching standard keyboard behaviour.
    LaunchedEffect(isLongPressing) {
        if (isLongPressing && (keyData.code == "BACKSPACE")) {
            while (isLongPressing) {
                (onKeyRepeat ?: onKeyClick)(keyData.code)
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
            .height(keyHeight)
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
                                if (keyData.secondaryLabel != null) {
                                    bubbleLabel = if (isShiftActive) keyData.secondaryLabel.kaaUppercase() else keyData.secondaryLabel
                                    showBubble = true
                                }
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
                .padding(horizontal = KeyboardDimensions.keyHorizontalPadding)
                .clip(KEY_SHAPE)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            when {
                effectiveIconResId != null -> {
                    Icon(
                        painter = painterResource(effectiveIconResId),
                        contentDescription = keyData.code,
                        tint = contentColor,
                        modifier = Modifier.size(KeyboardDimensions.actionIconSize)
                    )
                }
                keyData.hintText != null -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(KeyboardDimensions.hintKeySpacing)) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = keyData.displayText,
                            color = contentColor,
                            fontSize = KeyboardDimensions.hintKeyFontSize,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                        Text(
                            text = keyData.hintText,
                            modifier = Modifier.weight(1f),
                            color = contentColor.copy(alpha = 0.55f),
                            fontSize = KeyboardDimensions.hintFontSize,
                            letterSpacing = KeyboardDimensions.hintLetterSpacing,
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
                            KeyType.CHARACTER -> KeyboardDimensions.characterKeyFontSize
                            KeyType.MODIFIER -> KeyboardDimensions.modifierKeyFontSize
                            else -> KeyboardDimensions.actionKeyFontSize
                        },
                        fontWeight = when (keyData.keyType) {
                            KeyType.MODIFIER, KeyType.ACTION -> FontWeight.SemiBold
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
                        .padding(end = KeyboardDimensions.secondaryLabelEndPadding, top = KeyboardDimensions.secondaryLabelTopPadding),
                    color = contentColor.copy(alpha = 0.6f),
                    fontSize = KeyboardDimensions.secondaryLabelFontSize,
                    maxLines = 1,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Top,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )
            }
        }

        // Long-press bubble: floats above the key, drawn over the row above via
        // negative offset. Compose Column paints rows in order so this row draws
        // on top of the previous row — no Popup/separate window needed.
        if (showBubble && bubbleLabel.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = -(keyHeight + KeyboardDimensions.bubbleVerticalOffset))
                    .zIndex(1f)
                    .defaultMinSize(minWidth = keyHeight)
                    .height(keyHeight)
                    .shadow(KeyboardDimensions.bubbleShadowElevation, BUBBLE_SHAPE)
                    .background(colors.bubbleBackground, BUBBLE_SHAPE)
                    .padding(horizontal = KeyboardDimensions.bubbleHorizontalPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bubbleLabel,
                    color = colors.keyContent,
                    fontSize = KeyboardDimensions.bubbleFontSize,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}
