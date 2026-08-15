package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    onClose: () -> Unit,
) {
    val colors = LocalKeyboardColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.keyboardBackground)
    ) {
        ClipboardHeader(onClose = onClose)

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
                    ClipCard(clip = clip, onClick = { onClipClick(clip) })
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

@Composable
private fun ClipCard(
    clip: ClipItem,
    onClick: () -> Unit,
) {
    val colors = LocalKeyboardColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = KeyboardDimensions.clipCardMinHeight)
            .background(colors.modifierBackground, RoundedCornerShape(KeyboardDimensions.clipCardCornerRadius))
            .clickable(onClick = onClick)
            .padding(KeyboardDimensions.clipCardPadding),
    ) {
        Text(
            text = clip.text,
            color = colors.keyContent,
            fontSize = KeyboardDimensions.clipFontSize,
            maxLines = KeyboardDimensions.clipMaxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
