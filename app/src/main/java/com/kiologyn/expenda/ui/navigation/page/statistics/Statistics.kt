package com.kiologyn.expenda.ui.navigation.page.statistics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.ui.navigation.page.statistics.tab.START_TAB_INDEX
import com.kiologyn.expenda.ui.navigation.page.statistics.daterangepicker.DatePeriodSelectorContainer
import com.kiologyn.expenda.ui.navigation.page.statistics.tab.tabs
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.max


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Statistics() {
    val density = LocalDensity.current.density
    val fromDate = remember { mutableStateOf<LocalDateTime>(LocalDateTime.now().minusWeeks(1)) }
    val toDate = remember { mutableStateOf<LocalDateTime>(LocalDateTime.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
        ,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState(START_TAB_INDEX) { tabs.size }

        var fullWidthValue by remember { mutableFloatStateOf(0f) }
        var innerTabsWidth by remember { mutableStateOf(List(tabs.size) { 0 }) }
        ScrollableTabRow(
            modifier = Modifier.onSizeChanged { fullWidthValue = it.width.toFloat() },
            selectedTabIndex = pagerState.currentPage,
            edgePadding = max(0f, (fullWidthValue - innerTabsWidth.sum()) / 2 / density).dp,
            divider = { },
        ) {
            tabs.onEachIndexed { index, tab ->
                Tab(
                    modifier = Modifier.onSizeChanged {
                        innerTabsWidth = innerTabsWidth.toMutableList().apply {
                            this[index] = it.width
                        }.toList()
                    },
                    text = { Text(tab.name) },
                    selected = pagerState.settledPage == index,
                    onClick = { coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    } },
                )
            }
        }

        Box {
            val PICKER_HEIGHT = 90.dp

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { pageIndex ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    tabs[pageIndex].tab(this, fromDate, toDate)
                    item { Spacer(modifier = Modifier.height(PICKER_HEIGHT)) }
                }
            }


            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                DatePeriodSelectorContainer(fromDate, toDate, PICKER_HEIGHT)
            }
        }
    }
}


@Composable
fun StatisticContainer(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit,
) {
    val OUTER_PADDING = PaddingValues(10.dp)
    val SHAPE = RoundedCornerShape(20.dp)
    val INNER_PADDING = PaddingValues(20.dp, 15.dp)

    Surface(
        modifier = Modifier
            .padding(OUTER_PADDING)
            .fillMaxWidth()
        ,
    ) {
        Column(
            modifier = modifier
                .background(LocalExpendaColors.current.surfaceContainer, SHAPE)
                .padding(INNER_PADDING)
            ,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (title != null)
                Text(title, fontSize = 18.sp)

            content()
        }
    }
}


@Preview
@Composable
private fun Preview() = ExpendaTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) { Statistics() }
}
