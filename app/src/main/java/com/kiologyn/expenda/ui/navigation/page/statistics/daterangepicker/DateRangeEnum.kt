package com.kiologyn.expenda.ui.navigation.page.statistics.daterangepicker

import java.time.DayOfWeek
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters


internal enum class DateRangeEnum(
    val display: String,
    val chronoUnit: ChronoUnit,
    val temporalAdjusterToBeginning: TemporalAdjuster,
    val temporalAdjusterToEnd: TemporalAdjuster,
) {
    WEEK(
        "this week",
        ChronoUnit.WEEKS,
        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY),
        TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY),
    ),
    MONTH(
        "this month",
        ChronoUnit.MONTHS,
        TemporalAdjusters.firstDayOfMonth(),
        TemporalAdjusters.lastDayOfMonth(),
    ),
    YEAR(
        "this year",
        ChronoUnit.YEARS,
        TemporalAdjusters.firstDayOfYear(),
        TemporalAdjusters.lastDayOfYear(),
    ),
}
