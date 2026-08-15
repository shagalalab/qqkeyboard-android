package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.ClipItem
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors

/**
 * The clipboard history panel, shown over the keys the same way [EmojiLayout] is.
 *
 * Clips arrive already ordered — pinned first, then most recently copied — so the grid renders the
 * list as given.
 */
@Composable
fun ClipboardLayout(
    clips: List<ClipItem>,
    onClipClick: (ClipItem) -> Unit,
    onClipPinToggle: (ClipItem) -> Unit,
    onClipDelete: (ClipItem) -> Unit,
    onClose: () -> Unit,
) {
    val colors = LocalKeyboardColors.current

    // Long-pressing a clip selects it and swaps the header for its actions. Held here rather than
    // in the view model because it is purely about what this panel is showing — closing the panel
    // should forget it, and nothing outside these composables cares.
    var selected by remember { mutableStateOf<ClipItem?>(null) }

    // A selected clip that has just been deleted must not keep the action bar open.
    val selectedClip = selected?.let { s -> clips.firstOrNull { it.id == s.id } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.keyboardBackground)
    ) {
        if (selectedClip != null) {
            ClipActionBar(
                clip = selectedClip,
                onPinToggle = {
                    onClipPinToggle(selectedClip)
                    selected = null
                },
                onDelete = {
                    onClipDelete(selectedClip)
                    selected = null
                },
                onCancel = { selected = null },
            )
        } else {
            ClipboardHeader(onClose = onClose)
        }

        if (clips.isEmpty()) {
            ClipboardEmptyState()
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(KeyboardDimensions.clipboardGridColumns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = KeyboardDimensions.clipboardGridPadding,
                    vertical = KeyboardDimensions.clipboardGridPadding,
                ),
                verticalItemSpacing = KeyboardDimensions.clipCardSpacing,
                horizontalArrangement = Arrangement.spacedBy(KeyboardDimensions.clipCardSpacing),
            ) {
                items(items = clips, key = { it.id }) { clip ->
                    ClipCard(
                        clip = clip,
                        isSelected = clip.id == selectedClip?.id,
                        onClick = { onClipClick(clip) },
                        onLongClick = { selected = clip },
                    )
                }
            }
        }
    }
}

@Composable
private fun ClipboardHeader(onClose: () -> Unit) {
    val colors = LocalKeyboardColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(KeyboardDimensions.clipboardHeaderHeight)
            .padding(horizontal = KeyboardDimensions.clipboardHeaderPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.clipboard_title),
            color = colors.keyContent,
            style = MaterialTheme.typography.titleSmall,
        )

        Box(
            modifier = Modifier
                .background(colors.modifierBackground, CircleShape)
                .clickable { onClose() }
                .padding(KeyboardDimensions.categoryClosePadding),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.close_24px),
                contentDescription = null,
                tint = colors.keyContent,
                modifier = Modifier.size(KeyboardDimensions.categoryIconSize),
            )
        }
    }
}

@Composable
private fun ClipboardEmptyState() {
    val colors = LocalKeyboardColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = KeyboardDimensions.clipboardGridPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.clipboard_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.keyContent,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.clipboard_hint),
            style = MaterialTheme.typography.bodySmall,
            color = colors.keyContent.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * The actions for a long-pressed clip, replacing the header while one is selected.
 *
 * This is a strip inside the panel rather than a dropdown or context menu because those open their
 * own window — something [KeyButton] also avoids for its long-press bubble, which it draws inline.
 */
@Composable
private fun ClipActionBar(
    clip: ClipItem,
    onPinToggle: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    val colors = LocalKeyboardColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(KeyboardDimensions.clipboardHeaderHeight)
            .padding(horizontal = KeyboardDimensions.clipboardHeaderPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KeyboardDimensions.clipActionSpacing),
    ) {
        ClipAction(
            iconResId = if (clip.pinned) R.drawable.ic_pin_off else R.drawable.ic_pin,
            label = stringResource(if (clip.pinned) R.string.clip_unpin else R.string.clip_pin),
            onClick = onPinToggle,
        )
        ClipAction(
            iconResId = R.drawable.ic_trash,
            label = stringResource(R.string.clip_delete),
            onClick = onDelete,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier = Modifier
                    .background(colors.modifierBackground, CircleShape)
                    .clickable(onClick = onCancel)
                    .padding(KeyboardDimensions.categoryClosePadding),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.close_24px),
                    contentDescription = stringResource(R.string.clip_cancel),
                    tint = colors.keyContent,
                    modifier = Modifier.size(KeyboardDimensions.categoryIconSize),
                )
            }
        }
    }
}

@Composable
private fun ClipAction(
    iconResId: Int,
    label: String,
    onClick: () -> Unit,
) {
    val colors = LocalKeyboardColors.current
    Row(
        modifier = Modifier
            .background(colors.modifierBackground, CircleShape)
            .clickable(onClick = onClick)
            .padding(
                horizontal = KeyboardDimensions.clipActionHorizontalPadding,
                vertical = KeyboardDimensions.clipActionVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KeyboardDimensions.clipActionIconSpacing),
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null,
            tint = colors.keyContent,
            modifier = Modifier.size(KeyboardDimensions.clipActionIconSize),
        )
        Text(
            text = label,
            color = colors.keyContent,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClipCard(
    clip: ClipItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val colors = LocalKeyboardColors.current
    val shape = RoundedCornerShape(KeyboardDimensions.clipCardCornerRadius)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = KeyboardDimensions.clipCardMinHeight)
            .background(colors.modifierBackground, shape)
            .then(
                if (isSelected) {
                    Modifier.border(KeyboardDimensions.clipCardSelectedBorder, colors.keyContent, shape)
                } else {
                    Modifier
                }
            )
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(KeyboardDimensions.clipCardPadding),
    ) {
        Text(
            text = clip.text,
            color = colors.keyContent,
            fontSize = KeyboardDimensions.clipFontSize,
            maxLines = KeyboardDimensions.clipMaxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = if (clip.pinned) {
                Modifier.padding(end = KeyboardDimensions.clipPinBadgeSize)
            } else {
                Modifier
            },
        )

        if (clip.pinned) {
            Icon(
                painter = painterResource(R.drawable.ic_pin),
                contentDescription = null,
                tint = colors.keyContent.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(KeyboardDimensions.clipPinBadgeSize),
            )
        }
    }
}
