package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.KeyType
import com.shagalalab.qqkeyboard.keyboard.model.ShiftState
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardBorderEnabled
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.theme.toDp
import com.shagalalab.qqkeyboard.keyboard.utils.kaaUppercase
import kotlin.math.floor
import kotlinx.coroutines.delay

private const val REPEAT_INTERVAL_DELAY_MS = 50L
private const val BUBBLE_DISMISS_MS = 500L
private const val PICKER_CANCELLED = -1
private val BUBBLE_SHAPE = RoundedCornerShape(KeyboardDimensions.bubbleCornerRadius)
private val PICKER_CELL_SHAPE = RoundedCornerShape(KeyboardDimensions.bubbleCellCornerRadius)
private val KEY_SHAPE = RoundedCornerShape(KeyboardDimensions.keyCornerRadius)
private const val PICKER_HIGHLIGHT_ALPHA = 0.15f

/**
 * Maps the finger position (in key-local coordinates) onto a picker cell index, or
 * [PICKER_CANCELLED] when the finger has been dragged clear of the picker.
 *
 * The picker is laid out so that its first cell is centred on the key, which puts the resting
 * finger position squarely inside cell 0 — lifting without moving therefore always commits the
 * first alternate.
 */
private fun resolveAlternateIndex(
    position: Offset,
    keySize: IntSize,
    cellWidthPx: Float,
    cancelSlopPx: Float,
    count: Int
): Int {
    if (position.y > keySize.height + cancelSlopPx) return PICKER_CANCELLED
    val firstCellLeft = keySize.width / 2f - cellWidthPx / 2f
    return floor((position.x - firstCellLeft) / cellWidthPx).toInt().coerceIn(0, count - 1)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyButton(
    keyData: KeyData,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onKeyLongPress: ((String) -> Unit)? = null,
    onKeyRepeat: ((String) -> Unit)? = null,
    onAlternateHighlight: (() -> Unit)? = null,
    shiftState: ShiftState = ShiftState.OFF,
    topTouchPadding: Dp = 0.dp,
    bottomTouchPadding: Dp = 0.dp
) {
    val isShiftActive = shiftState != ShiftState.OFF
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isLongPressing by remember { mutableStateOf(false) }
    var showBubble by remember { mutableStateOf(false) }
    var bubbleLabel by remember { mutableStateOf("") }

    val alternates = keyData.alternativeChars
    // A single alternate commits on long press; several need the user to pick between them.
    val hasPicker = alternates.size > 1
    var isPickerOpen by remember { mutableStateOf(false) }
    var highlightedIndex by remember { mutableIntStateOf(0) }
    val currentLongPress by rememberUpdatedState(onKeyLongPress)
    val currentHighlightFeedback by rememberUpdatedState(onAlternateHighlight)

    val colors = LocalKeyboardColors.current
    val keyHeight = LocalKeyboardHeight.current.toDp()
    val isLandscape = LocalKeyboardHeight.current == KeyboardHeight.LANDSCAPE
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

    // Keys with a picker need the finger tracked after the long press fires, which
    // combinedClickable cannot do. This observer runs alongside it: it never consumes anything,
    // so the click/long-click detection above is left exactly as it is for every other key.
    val pickerModifier = if (hasPicker) {
        Modifier.pointerInput(alternates) {
            val cellWidthPx = KeyboardDimensions.bubbleCellWidth.toPx()
            val cancelSlopPx = KeyboardDimensions.bubbleCancelSlop.toPx()
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                var index = 0
                highlightedIndex = 0
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    val resolved = resolveAlternateIndex(
                        change.position, size, cellWidthPx, cancelSlopPx, alternates.size
                    )
                    if (resolved != index) {
                        index = resolved
                        highlightedIndex = resolved
                        if (isPickerOpen) currentHighlightFeedback?.invoke()
                    }
                    if (!change.pressed) break
                }
                if (isPickerOpen) {
                    isPickerOpen = false
                    if (index != PICKER_CANCELLED) currentLongPress?.invoke(alternates[index])
                }
            }
        }
    } else Modifier

    // Outer box: full allocated width and height (including the inter-row gap absorbed via
    // top/bottomTouchPadding) — this is the touch target.
    // Inner box: inset by keyHorizontalPadding on each side and by the touch paddings top/bottom
    // — this is what the user sees.
    // Adjacent keys' outer boxes are flush both horizontally and vertically (0dp gap), so there
    // is no dead zone between keys, while the visual gaps are preserved via the inner insets.
    Box(
        modifier = modifier
            .height(keyHeight + topTouchPadding + bottomTouchPadding)
            .let { m ->
                if (keyData.code != "SPACER") {
                    m.combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onKeyClick(keyData.code) },
                        onLongClick = {
                            when {
                                keyData.code == "BACKSPACE" -> isLongPressing = true
                                // Nothing is committed yet — the picker waits for the release.
                                // The tick stands in for the key press feedback the user would
                                // otherwise have felt at this point.
                                hasPicker -> {
                                    isPickerOpen = true
                                    onAlternateHighlight?.invoke()
                                }
                                else -> {
                                    alternates.firstOrNull()?.let { alternate ->
                                        bubbleLabel = if (isShiftActive) alternate.kaaUppercase() else alternate
                                        showBubble = true
                                    }
                                    onKeyLongPress?.invoke(alternates.firstOrNull() ?: keyData.code)
                                }
                            }
                        }
                    ).then(pickerModifier)
                } else m
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = KeyboardDimensions.keyHorizontalPadding,
                    end = KeyboardDimensions.keyHorizontalPadding,
                    top = topTouchPadding,
                    bottom = bottomTouchPadding
                )
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
                            fontSize = if (isLandscape) KeyboardDimensions.hintKeyFontSizeLandscape else KeyboardDimensions.hintKeyFontSize,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                        Text(
                            text = keyData.hintText,
                            modifier = Modifier.weight(1f),
                            color = contentColor.copy(alpha = 0.55f),
                            fontSize = if (isLandscape) KeyboardDimensions.hintFontSizeLandscape else KeyboardDimensions.hintFontSize,
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
                            KeyType.CHARACTER -> if (isLandscape) KeyboardDimensions.characterKeyFontSizeLandscape else KeyboardDimensions.characterKeyFontSize
                            KeyType.MODIFIER -> if (isLandscape) KeyboardDimensions.modifierKeyFontSizeLandscape else KeyboardDimensions.modifierKeyFontSize
                            else -> if (isLandscape) KeyboardDimensions.actionKeyFontSizeLandscape else KeyboardDimensions.actionKeyFontSize
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
            if (alternates.isNotEmpty()) {
                Text(
                    text = alternates.joinToString(" "),
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
                    fontSize = if (isLandscape) KeyboardDimensions.bubbleFontSizeLandscape else KeyboardDimensions.bubbleFontSize,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }
        }

        // Picker bubble: same placement trick as above, but shifted right by half a cell so the
        // first cell — the default choice — sits directly over the key.
        // unbounded = true is essential: without it the row is measured against the key's own
        // width and every cell past the first is squeezed out. Centring the oversized row on the
        // key and then shifting it by half a cell leaves the first cell exactly over the key,
        // which is the geometry resolveAlternateIndex assumes.
        if (isPickerOpen) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        x = KeyboardDimensions.bubbleCellWidth * (alternates.size - 1) / 2,
                        y = -(keyHeight + KeyboardDimensions.bubbleVerticalOffset)
                    )
                    .zIndex(1f)
                    .wrapContentWidth(align = Alignment.CenterHorizontally, unbounded = true)
                    .height(keyHeight)
                    .shadow(KeyboardDimensions.bubbleShadowElevation, BUBBLE_SHAPE)
                    .background(colors.bubbleBackground, BUBBLE_SHAPE)
            ) {
                alternates.forEachIndexed { index, alternate ->
                    Box(
                        modifier = Modifier
                            .width(KeyboardDimensions.bubbleCellWidth)
                            .fillMaxHeight()
                            .padding(KeyboardDimensions.bubbleCellInset)
                            .clip(PICKER_CELL_SHAPE)
                            .background(
                                if (index == highlightedIndex) {
                                    colors.keyContent.copy(alpha = PICKER_HIGHLIGHT_ALPHA)
                                } else {
                                    Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isShiftActive) alternate.kaaUppercase() else alternate,
                            color = colors.keyContent,
                            fontSize = if (isLandscape) KeyboardDimensions.bubbleFontSizeLandscape else KeyboardDimensions.bubbleFontSize,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
