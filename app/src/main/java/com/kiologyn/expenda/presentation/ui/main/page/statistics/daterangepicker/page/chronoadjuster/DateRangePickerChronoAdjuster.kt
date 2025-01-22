package com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page.chronoadjuster

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.utils.format
import com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page.DateRangePickerPageContainer
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


@Composable
fun DateRangePickerPageChronoAdjuster(
    dateRange: ClosedRange<LocalDate>,
    onDateRangePick: (newDateRange: ClosedRange<LocalDate>) -> Unit,
    dateRangeAdjuster: DateRangeAdjuster,
    onDateRangeAdjusterPick: (newDateRange: DateRangeAdjuster) -> Unit,
) = DateRangePickerPageContainer {
    val resetShift: () -> Unit = {
        LocalDate.now().let {
            onDateRangePick(it.with(dateRangeAdjuster.temporalAdjusterToStart)..it.with(dateRangeAdjuster.temporalAdjusterToEnd))
        }
    }
    LaunchedEffect(dateRangeAdjuster) {
        resetShift()
    }

    val labelText = if (
        dateRange.start.compareTo(
            LocalDate.now().with(dateRangeAdjuster.temporalAdjusterToStart)
        ) == 0
    ) dateRangeAdjuster.display()
    else when (dateRangeAdjuster) {
        DateRangeAdjuster.WEEK -> "${dateRange.start.format(Helper.DATE_FORMAT)} - ${dateRange.endInclusive.format(Helper.DATE_FORMAT)}"
        DateRangeAdjuster.MONTH -> dateRange.start.format(Helper.DATE_MY_FORMAT)
        DateRangeAdjuster.YEAR -> dateRange.start.year.toString()
    }

    val shiftDates: (Boolean) -> Unit = { isForward ->
        val localFromDate = dateRange.start.plus(if (isForward) 1 else -1, dateRangeAdjuster.chronoUnit)
        var localToDate = dateRange.endInclusive.plus(if (isForward) 1 else -1, dateRangeAdjuster.chronoUnit)

        localToDate = localToDate.run { when (dateRangeAdjuster) {
            DateRangeAdjuster.MONTH -> with(TemporalAdjusters.lastDayOfMonth())
            DateRangeAdjuster.YEAR -> with(TemporalAdjusters.lastDayOfYear())
            else -> this
        } }

        onDateRangePick(localFromDate..localToDate)
    }

    ProvideTextStyle(
        TextStyle(color = LocalExpendaColors.current.dateRangePickerStroke)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable { shiftDates(false) }
                ,
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = LocalTextStyle.current.color,
                )
            }

            var menuOpened by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f)
                    .background(LocalExpendaColors.current.dateRangePickerStroke)
                    .clickable { menuOpened = true }
                ,
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = labelText,
                    color = LocalExpendaColors.current.periodPicker,
                    fontWeight = FontWeight.Bold,
                )

                DropdownMenu(
                    expanded = menuOpened,
                    onDismissRequest = { menuOpened = false },
                ) {
                    DateRangeAdjuster.entries.forEach { dateRangeAdjusterOption ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = dateRangeAdjusterOption.display(),
                                    fontSize = 18.sp,
                                )
                            },
                            onClick = {
                                if (dateRangeAdjusterOption == dateRangeAdjuster)
                                    resetShift()
                                else
                                    onDateRangeAdjusterPick(dateRangeAdjusterOption)
                                menuOpened = false
                            },
                            modifier = Modifier.padding(horizontal = 10.dp),
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable { shiftDates(true) }
                ,
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = LocalTextStyle.current.color,
                )
            }
        }
    }
}