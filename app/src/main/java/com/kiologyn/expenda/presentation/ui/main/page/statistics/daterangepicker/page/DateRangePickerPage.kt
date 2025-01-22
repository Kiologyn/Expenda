package com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page.chronoadjuster.DateRangeAdjuster
import com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page.chronoadjuster.DateRangePickerPageChronoAdjuster
import com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page.customrange.DateRangePickerPageCustomRange
import java.time.LocalDate


sealed class DateRangePickerPage(
    var dateRange: MutableState<ClosedRange<LocalDate>>,
    inline val content: @Composable (
        dateRange: ClosedRange<LocalDate>,
        onDateRangePick: (newDateRange: ClosedRange<LocalDate>) -> Unit,
    ) -> Unit,
) {
    class ChronoAdjuster(
        private val dateRangeAdjuster: MutableState<DateRangeAdjuster> = mutableStateOf(DateRangeAdjuster.WEEK),
    ) : DateRangePickerPage(
        dateRange = mutableStateOf(
            LocalDate.now().with(dateRangeAdjuster.value.temporalAdjusterToStart)..LocalDate.now().with(dateRangeAdjuster.value.temporalAdjusterToEnd)
        ),
        content = { dateRange, onDateRangePick ->
            DateRangePickerPageChronoAdjuster(
                dateRange = dateRange,
                onDateRangePick = onDateRangePick,
                dateRangeAdjuster = dateRangeAdjuster.value,
                onDateRangeAdjusterPick = { newDateRangeAdjuster ->
                    dateRangeAdjuster.value = newDateRangeAdjuster
                },
            )
        },
    )
    class CustomRange : DateRangePickerPage(
        dateRange = mutableStateOf(
            LocalDate.now().minusWeeks(1)..LocalDate.now()
        ),
        content = { dateRange, onDateRangePick ->
            DateRangePickerPageCustomRange(
                dateRange,
                onDateRangePick,
            )
        },
    )
}
