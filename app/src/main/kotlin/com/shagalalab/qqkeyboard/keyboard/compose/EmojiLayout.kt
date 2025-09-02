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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.data.EmojiData
import kotlinx.coroutines.launch

const val COLLECTION_GRID_COLS_SIZE = 10
const val EMOJI_KEY_SIZE = 36
const val KEY_VPADDING_SIZE = 6
const val KEY_HPADDING_SIZE = 4

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // Category navigation row
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button
            Box(
                modifier = Modifier
                    .clickable { onCloseEmojiLayout() }
                    .padding(horizontal = KEY_HPADDING_SIZE.dp, vertical = KEY_VPADDING_SIZE.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_circle_arrow_left),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Category icons
            categories.forEachIndexed { index, categoryIconResId ->
                val isActive = pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                        .padding(horizontal = KEY_HPADDING_SIZE.dp, vertical = KEY_VPADDING_SIZE.dp),
                ) {
                    Icon(
                        painter = painterResource(categoryIconResId),
                        contentDescription = null,
                        tint = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clickable { onKeyClick("BACKSPACE") }
                    .padding(horizontal = KEY_HPADDING_SIZE.dp, vertical = KEY_VPADDING_SIZE.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Horizontal pager for emoji categories
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            val categoryIcon = categories[page]
            val emojis = EmojiData.getEmojisWithCategories(recentEmojis)[categoryIcon] ?: emptyList()

            EmojiCategoryPage(
                emojis = emojis,
                onKeyClick = onKeyClick,
                isRecentCategory = categoryIcon == R.drawable.ic_clock_3
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
        // Empty state for recent emojis
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_recent_emojis),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        // Emoji grid
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
                        .height(EMOJI_KEY_SIZE.dp)
                        .clickable(interactionSource = null, indication = null) { onKeyClick(emoji) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
