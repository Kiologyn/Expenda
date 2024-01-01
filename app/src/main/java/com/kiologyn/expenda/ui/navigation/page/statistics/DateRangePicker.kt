package com.kiologyn.expenda.ui.navigation.page.statistics

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.formatDate
import com.kiologyn.expenda.toLocalDateTime
import com.kiologyn.expenda.toMilliseconds
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DatePeriodSelectorContainer(
    fromDate: MutableState<LocalDateTime> = remember { mutableStateOf(LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) },
    toDate: MutableState<LocalDateTime> = remember { mutableStateOf(LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))) },
) {
    val PAGES_COUNT = 2
    val INITIAL_PAGE_INDEX = 0
    val CONTAINER_BORDER_RADUIS = 30.dp
    val CONTAINER_SHAPE = RoundedCornerShape(CONTAINER_BORDER_RADUIS, CONTAINER_BORDER_RADUIS)

    val pagerState = rememberPagerState(initialPage = INITIAL_PAGE_INDEX) { PAGES_COUNT }
    var selectedPageIndex by remember { mutableIntStateOf(INITIAL_PAGE_INDEX) }
    var selectedTimeUnit by remember { mutableStateOf(DropdownMenuEnum.WEEK) }
    val datePeriodsStates: List<Map<String, MutableState<LocalDateTime>>> = listOf(
        mapOf(
            "from" to remember { mutableStateOf(LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) },
            "to" to remember { mutableStateOf(LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))) },
        ),
        mapOf(
            "from" to remember { mutableStateOf(LocalDateTime.now().minusWeeks(1)) },
            "to" to remember { mutableStateOf(LocalDateTime.now()) },
        ),
    )

    LaunchedEffect(pagerState.currentPage) {
        selectedPageIndex = pagerState.currentPage
    }
    LaunchedEffect(pagerState.settledPage) {
        fromDate.value = datePeriodsStates[pagerState.currentPage]["from"]!!.value
        toDate.value = datePeriodsStates[pagerState.currentPage]["to"]!!.value
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                LocalExpendaColors.current.periodPicker,
                CONTAINER_SHAPE,
            )
    ) {
        Box(modifier = Modifier.padding(top = 15.dp, bottom = 10.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val HEIGHT = 40.dp
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HEIGHT * 2f))
                    ,
                ) {pageIndex ->
                    val modifier = Modifier.fillMaxHeight()
                    val datePeriodStates = datePeriodsStates[pageIndex]
                    var localFromDate by datePeriodStates["from"]!!
                    var localToDate by datePeriodStates["to"]!!
                    LaunchedEffect(localFromDate, localToDate) {
                        if (pageIndex != pagerState.settledPage) return@LaunchedEffect
                        fromDate.value = localFromDate
                        toDate.value = localToDate
                    }
                    when (pageIndex) {
                        0 -> TimePeriodSelectorElementContainer(HEIGHT) {
                            var menuOpened by remember { mutableStateOf(false) }

                            LaunchedEffect(selectedTimeUnit) {
                                if (pageIndex != pagerState.settledPage) return@LaunchedEffect
                                val localDateTimeNow = LocalDateTime.now()
                                when (selectedTimeUnit) {
                                    DropdownMenuEnum.WEEK -> {
                                        localFromDate = localDateTimeNow.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                        localToDate = localDateTimeNow.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                                    }
                                    DropdownMenuEnum.MONTH -> {
                                        localFromDate = localDateTimeNow.with(TemporalAdjusters.firstDayOfMonth())
                                        localToDate = localDateTimeNow.with(TemporalAdjusters.lastDayOfMonth())
                                    }
                                    DropdownMenuEnum.YEAR -> {
                                        localFromDate = localDateTimeNow.with(TemporalAdjusters.firstDayOfYear())
                                        localToDate = localDateTimeNow.with(TemporalAdjusters.lastDayOfYear())
                                    }
                                }
                            }

                            var labelText by remember { mutableStateOf("") }
                            LaunchedEffect(localFromDate, localToDate) {
                                labelText =
                                    if (
                                        localFromDate.toLocalDate().compareTo(
                                            LocalDate.now().run {
                                                when (selectedTimeUnit) {
                                                    DropdownMenuEnum.WEEK -> with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                                    DropdownMenuEnum.MONTH -> with(TemporalAdjusters.firstDayOfMonth())
                                                    DropdownMenuEnum.YEAR -> with(TemporalAdjusters.firstDayOfYear())
                                                }
                                            }
                                        ) == 0
                                    ) selectedTimeUnit.display
                                    else when (selectedTimeUnit) {
                                        DropdownMenuEnum.WEEK -> "${localFromDate.formatDate()} - ${localToDate.formatDate()}"
                                        DropdownMenuEnum.MONTH -> "${localFromDate.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())} ${localFromDate.year}"
                                        DropdownMenuEnum.YEAR -> "${localFromDate.year}"
                                    }
                            }

                            val shiftDates: (Boolean) -> Unit = { isForward ->
                                when (selectedTimeUnit) {
                                    DropdownMenuEnum.WEEK -> {
                                        localFromDate = (
                                            if (isForward) localFromDate.plusWeeks(1)
                                            else localFromDate.minusWeeks(1)
                                        )
                                        localToDate = (
                                            if (isForward) localToDate.plusWeeks(1)
                                            else localToDate.minusWeeks(1)
                                        )
                                    }
                                    DropdownMenuEnum.MONTH -> {
                                        localFromDate = (
                                            if (isForward) localFromDate.plusMonths(1)
                                            else localFromDate.minusMonths(1)
                                        )
                                        localToDate = (
                                            if (isForward) localToDate.plusMonths(1)
                                            else localToDate.minusMonths(1)
                                        ).with(TemporalAdjusters.lastDayOfMonth())
                                    }
                                    DropdownMenuEnum.YEAR -> {
                                        localFromDate = (
                                            if (isForward) localFromDate.plusYears(1)
                                            else localFromDate.minusYears(1)
                                        )
                                        localToDate = (
                                            if (isForward) localToDate.plusYears(1)
                                            else localToDate.minusYears(1)
                                        ).with(TemporalAdjusters.lastDayOfYear())
                                    }
                                }
                            }

                            ProvideTextStyle(TextStyle(
                                color = LocalExpendaColors.current.dateRangePickerStroke,
                            )) {
                                Box(
                                    modifier = modifier
                                        .weight(1f)
                                        .clickable { shiftDates(false) }
                                    ,
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowLeft,
                                        contentDescription = "left arrow",
                                        tint = LocalTextStyle.current.color,
                                    )
                                }
                                Box(
                                    modifier = modifier
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
                                        DropdownMenuEnum.values().forEach { item ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = item.display,
                                                        fontSize = 18.sp,
                                                    )
                                                },
                                                onClick = {
                                                    selectedTimeUnit = item
                                                    menuOpened = false
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 10.dp)
                                                ,
                                            )
                                        }
                                    }
                                }
                                Box(
                                    modifier = modifier
                                        .weight(1f)
                                        .clickable { shiftDates(true) }
                                    ,
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "right arrow",
                                        tint = LocalTextStyle.current.color,
                                    )
                                }
                            }
                        }
                        1 -> TimePeriodSelectorElementContainer(HEIGHT) {
                            var fromDateText by remember { mutableStateOf(localFromDate.formatDate()) }
                            LaunchedEffect(localFromDate) { fromDateText = localFromDate.formatDate() }

                            var toDateText by remember { mutableStateOf(localToDate.formatDate()) }
                            LaunchedEffect(localToDate) { toDateText = localToDate.formatDate() }

                            var datePickerState by remember { mutableIntStateOf(0) }
                            val fromDatePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = localFromDate.toMilliseconds()
                            )
                            val toDatePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = localToDate.toMilliseconds()
                            )

                            if (datePickerState != 0)
                                DatePickerDialog(
                                    onDismissRequest = { datePickerState = 0 },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                (
                                                    if (datePickerState == 1) datePeriodStates["from"]!!
                                                    else datePeriodStates["to"]!!
                                                ).value = (
                                                    if (datePickerState == 1) fromDatePickerState
                                                    else toDatePickerState
                                                ).selectedDateMillis!!.toLocalDateTime()
                                                datePickerState = 0
                                            },
                                        ) {
                                            Text("OK")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { datePickerState = 0 }) {
                                            Text("Cancel")
                                        }
                                    },
                                ) {
                                    DatePicker(
                                        if (datePickerState == 1) fromDatePickerState
                                        else toDatePickerState
                                    )
                                }

                            ProvideTextStyle(TextStyle(
                                color = LocalExpendaColors.current.dateRangePickerStroke,
                            )) {
                                Box(
                                    modifier = modifier
                                        .weight(1f)
                                        .clickable {
                                            datePickerState = 1
                                        }
                                    ,
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(fromDateText)
                                }
                                Box(
                                    modifier = modifier
                                        .fillMaxWidth(0.2f)
                                        .background(LocalExpendaColors.current.dateRangePickerStroke)
                                    ,
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "-",
                                        color = LocalExpendaColors.current.periodPicker,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp,
                                        fontFamily = FontFamily.SansSerif,
                                    )
                                }
                                Box(
                                    modifier = modifier
                                        .weight(1f)
                                        .clickable {
                                            datePickerState = 2
                                        }
                                    ,
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(toDateText)
                                }
                            }
                        }
                    }
                }

                DotsIndicator(
                    totalDots = PAGES_COUNT,
                    selectedIndex = selectedPageIndex,
                    selectedColor = LocalExpendaColors.current.dotIndicatorSelected,
                    unSelectedColor = LocalExpendaColors.current.dotIndicatorUnselected,
                    size = 10.dp,
                )
            }
        }
    }
}
@Composable
fun TimePeriodSelectorElementContainer(
    height: Dp = 50.dp,
    content: @Composable RowScope.() -> Unit = {},
) {
    val SHAPE = RoundedCornerShape(height*2f)
    val STROKE_WIDTH = 2.dp
    val STROKE_COLOR = LocalExpendaColors.current.dateRangePickerStroke

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .border(STROKE_WIDTH, STROKE_COLOR, SHAPE)
            .clip(SHAPE)
        ,
        content = content,
    )
}
@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots : Int,
    selectedIndex : Int,
    selectedColor: Color,
    unSelectedColor: Color,
    size: Dp,
){
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
        ,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(totalDots) { index ->
            val isActive = index == selectedIndex
            val sizeAnimation by remember { mutableStateOf(Animatable(
                if (isActive) size.value * 2
                else size.value
            )) }
            val colorAnimation by remember { mutableStateOf(Animatable(
                if (isActive) selectedColor
                else unSelectedColor
            )) }

            val ANIMATION_DURATION = 25
            LaunchedEffect(index == selectedIndex) {
                sizeAnimation.animateTo(
                    targetValue =
                        if (isActive) size.value * 2
                        else size.value
                    ,
                    animationSpec = tween(
                        durationMillis = ANIMATION_DURATION,
                        easing = LinearEasing,
                    ),
                )
                colorAnimation.animateTo(
                    targetValue =
                        if (isActive) selectedColor
                        else unSelectedColor
                    ,
                    animationSpec = tween(
                        durationMillis = ANIMATION_DURATION,
                        easing = LinearEasing,
                    ),
                )
            }

            Box(
                modifier = Modifier
                    .height(size)
                    .width(sizeAnimation.value.dp)
                    .clip(CircleShape)
                    .background(colorAnimation.value)
                ,
            )

            if (index != totalDots - 1)
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
        }
    }
}


@Preview
@Composable
private fun Preview() = ExpendaTheme {
    DatePeriodSelectorContainer()
}
