package com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaDotsPageIndicator
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors
import java.time.LocalDate


@Composable
fun DateRangePicker(
    modifier: Modifier = Modifier,
    viewModel: DateRangePickerViewModel = viewModel(),
    height: Dp = 100.dp,
) {
    DateRangePickerContainer(modifier = modifier, height = height) { pickerHeight ->
        DateRangePickerElement(
            viewModel,
            pickerHeight,
        )
    }
}

@Composable
private fun DateRangePickerContainer(
    modifier: Modifier = Modifier,
    height: Dp,
    content: @Composable (height: Dp) -> Unit,
) {
    val CONTENT_PERCENT = 0.7f

    val shape = RoundedCornerShape(topStartPercent = 100/3, topEndPercent = 100/3)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                color = LocalExpendaColors.current.periodPicker,
                shape = shape,
            )
            .clip(shape)
        ,
    ) {
        Box(modifier = Modifier.padding(height * (1 - CONTENT_PERCENT) / 2)) {
            content(height * CONTENT_PERCENT)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DateRangePickerElement(
    viewModel: DateRangePickerViewModel = viewModel(),
    height: Dp,
) {
    val pagePercent = 0.675f
    val dotsPercent = 0.175f

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(height * (1 - (pagePercent + dotsPercent))),
    ) {
        val pageCount = viewModel.getPageCount()

        val pagerState = rememberPagerState(pageCount = { pageCount })

        viewModel.changePage(pagerState.settledPage)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(height * pagePercent)
                .clip(RoundedCornerShape(height / 2))
            ,
        ) { pageIndex ->
            val page = viewModel.getPage(pageIndex)
            page.content(
                page.dateRange.value,
                { newDateRange: ClosedRange<LocalDate> ->
                    page.dateRange.value = newDateRange
                }
            )
        }

        ExpendaDotsPageIndicator(
            totalDots = pageCount,
            selectedIndex = pagerState.currentPage,
            selectedColor = LocalExpendaColors.current.dotIndicatorSelected,
            unSelectedColor = LocalExpendaColors.current.dotIndicatorUnselected,
            size = height * dotsPercent,
        )
    }
}
