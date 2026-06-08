package com.shagalalab.qqkeyboard.keyboard.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.data.EmojiData
import com.shagalalab.qqkeyboard.keyboard.theme.LocalKeyboardColors
import kotlinx.coroutines.launch

const val COLLECTION_GRID_COLS_SIZE = 9
private const val EMOJI_KEY_HEIGHT = 48
private const val EMOJI_FONT_SIZE = 28
private const val CATEGORY_ICON_SIZE = 24
private const val CATEGORY_VERTICAL_PADDING = 6

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiLayout(
    onKeyClick: (String) -> Unit,
    onCloseEmojiLayout: () -> Unit,
    recentEmojis: List<String> = emptyList()
) {
    val emojiMap = remember(recentEmojis) { EmojiData.getEmojisWithCategories(recentEmojis) }
    val categories = remember(emojiMap) { emojiMap.keys.toList() }
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val coroutineScope = rememberCoroutineScope()
    val colors = LocalKeyboardColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.keyboardBackground)
    ) {
        CategoryNavigationRow(
            categories = categories,
            pagerState = pagerState,
            onCategoryClick = { index -> coroutineScope.launch { pagerState.animateScrollToPage(index) } },
            onClose = onCloseEmojiLayout,
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val categoryIcon = categories[page]
            val emojis = emojiMap[categoryIcon] ?: emptyList()

            EmojiCategoryPage(
                emojis = emojis,
                onKeyClick = onKeyClick,
                isRecentCategory = categoryIcon == R.drawable.category_recent
            )
        }
    }
}

// Isolated into its own composable so that pagerState.targetPage reads — which fire on every
// scroll frame — only recompose the row, not EmojiLayout and the HorizontalPager below it.
@Composable
private fun CategoryNavigationRow(
    categories: List<Int>,
    pagerState: PagerState,
    onCategoryClick: (Int) -> Unit,
    onClose: () -> Unit,
) {
    val colors = LocalKeyboardColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        categories.forEachIndexed { index, categoryIconResId ->
            // pagerState.targetPage is read here — CategoryNavigationRow is the recompose scope,
            // so only this row reruns per frame, not EmojiLayout.
            CategoryIcon(
                iconResId = categoryIconResId,
                isActive = pagerState.targetPage == index,
                iconColor = colors.keyContent,
                onClick = { onCategoryClick(index) },
            )
        }

        Box(
            modifier = Modifier
                .background(colors.modifierBackground, CircleShape)
                .clickable { onClose() }
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.close_24px),
                contentDescription = null,
                tint = colors.keyContent,
                modifier = Modifier.size(CATEGORY_ICON_SIZE.dp),
            )
        }
    }
}

// Extracted so icons whose isActive hasn't changed can skip recomposition while the row reruns.
@Composable
private fun CategoryIcon(
    iconResId: Int,
    isActive: Boolean,
    iconColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable(indication = null, interactionSource = null, onClick = onClick)
            .padding(vertical = CATEGORY_VERTICAL_PADDING.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size((CATEGORY_ICON_SIZE + 4).dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = iconColor.copy(alpha = if (isActive) 1f else 0.3f),
                modifier = Modifier.size(CATEGORY_ICON_SIZE.dp)
            )
        }
    }
}

@Composable
private fun EmojiCategoryPage(
    emojis: List<String>,
    onKeyClick: (String) -> Unit,
    isRecentCategory: Boolean = false
) {
    if (isRecentCategory && emojis.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_recent_emojis),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalKeyboardColors.current.keyContent,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(COLLECTION_GRID_COLS_SIZE),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = emojis,
                key = { emoji -> emoji }
            ) { emoji ->
                Box(
                    modifier = Modifier
                        .height(EMOJI_KEY_HEIGHT.dp)
                        .clickable(interactionSource = null, indication = null) { onKeyClick(emoji) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = emoji,
                        fontSize = EMOJI_FONT_SIZE.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
