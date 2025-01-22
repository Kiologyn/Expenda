package com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page.chronoadjuster

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kiologyn.expenda.R
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters


enum class DateRangeAdjuster(
    val display: @Composable () -> String,
    val chronoUnit: ChronoUnit,
    val temporalAdjusterToStart: TemporalAdjuster,
    val temporalAdjusterToEnd: TemporalAdjuster,
) {
    WEEK(
        { stringResource(R.string.date_range_picker__chrono_adjuster__this_week) },
        ChronoUnit.WEEKS,
        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY),
        TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY),
    ),
    MONTH(
        { stringResource(R.string.date_range_picker__chrono_adjuster__this_month) },
        ChronoUnit.MONTHS,
        TemporalAdjusters.firstDayOfMonth(),
        TemporalAdjusters.lastDayOfMonth(),
    ),
    YEAR(
        { stringResource(R.string.date_range_picker__chrono_adjuster__this_year) },
        ChronoUnit.YEARS,
        TemporalAdjusters.firstDayOfYear(),
        TemporalAdjusters.lastDayOfYear(),
    ),
}
