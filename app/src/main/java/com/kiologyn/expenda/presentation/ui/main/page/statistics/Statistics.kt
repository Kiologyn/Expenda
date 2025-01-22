package com.kiologyn.expenda.presentation.ui.main.page.statistics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.StatisticsTab
import com.kiologyn.expenda.presentation.common.sharedcomponent.CenteredScrollableTabRow
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Statistics(
    dateRange: ClosedRange<LocalDate>,
    pickerHeight: Dp,
) = Column(modifier = Modifier.fillMaxSize()) {
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = StatisticsTab.entries.first().ordinal,
        pageCount = { StatisticsTab.entries.size },
    )

    CenteredScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        onTabClick = { tabIndex ->
            coroutineScope.launch {
                pagerState.animateScrollToPage(tabIndex)
            }
        },
        tabs = StatisticsTab.entries.map { it.displayName() },
    )
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
    ) { tabIndex ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            StatisticsTab.entries[tabIndex].content(this, dateRange)
            item { Spacer(modifier = Modifier.height(pickerHeight)) }
        }
    }
}