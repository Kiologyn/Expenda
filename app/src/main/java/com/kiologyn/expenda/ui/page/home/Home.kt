package com.kiologyn.expenda.ui.page.home

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch


private data class TabItem(
    val name: String,
    val tab: @Composable () -> Unit,
)

private val tabs: List<TabItem> = listOf(
    TabItem(
        name = "Overview",
        tab = { Overview() },
    ),
    TabItem(
        name = "Statistics",
        tab = { Statistics() },
    ),
)
private const val START_TAB_INDEX = 0

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home() {
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(START_TAB_INDEX) }
    val pagerState = rememberPagerState(initialPage = selectedTabIndex) { tabs.size }
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.onEachIndexed { index, tab ->
                Tab(
                    text = { Text(tab.name) },
                    selected = index == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = index
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                )
            }
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
                selectedTabIndex = pageIndex
            }
        }
        HorizontalPager(state = pagerState) { pageIndex ->
            tabs[pageIndex].tab()
        }
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun HomePreview() = Home()