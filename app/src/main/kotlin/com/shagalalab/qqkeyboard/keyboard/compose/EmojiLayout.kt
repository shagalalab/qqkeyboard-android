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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
private const val CATEGORY_VERTICAL_PADDING = 8

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiLayout(
    onKeyClick: (String) -> Unit,
    onCloseEmojiLayout: () -> Unit,
    recentEmojis: List<String> = emptyList()
) {
    val categories = EmojiData.getEmojisWithCategories(recentEmojis).keys.toList()
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val coroutineScope = rememberCoroutineScope()
    val colors = LocalKeyboardColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.keyboardBackground)
    ) {
        // Category navigation row
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Category icons
            categories.forEachIndexed { index, categoryIconResId ->
                val isActive = pagerState.targetPage == index

                Box(
                    modifier = Modifier
                        .clickable(indication = null, interactionSource = null) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                        .padding(vertical = CATEGORY_VERTICAL_PADDING.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size((CATEGORY_ICON_SIZE + 4).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(categoryIconResId),
                            contentDescription = null,
                            tint = colors.keyContent.copy(alpha = if (isActive) 1f else 0.3f),
                            modifier = Modifier.size(CATEGORY_ICON_SIZE.dp)
                        )
                    }
                }
            }

            // Close button
            Box(
                modifier = Modifier
                    .background(colors.modifierBackground, CircleShape)
                    .clickable { onCloseEmojiLayout() }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.close_24px),
                    contentDescription = null,
                    tint = colors.keyContent,
                    modifier = Modifier.size(CATEGORY_ICON_SIZE.dp)
                )
            }
        }

        // Horizontal pager for emoji categories
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val categoryIcon = categories[page]
            val emojis = EmojiData.getEmojisWithCategories(recentEmojis)[categoryIcon] ?: emptyList()

            EmojiCategoryPage(
                emojis = emojis,
                onKeyClick = onKeyClick,
                isRecentCategory = categoryIcon == R.drawable.category_recent
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
