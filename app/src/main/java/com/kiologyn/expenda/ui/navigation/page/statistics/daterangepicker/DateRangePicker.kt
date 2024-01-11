package com.kiologyn.expenda.ui.navigation.page.statistics.daterangepicker

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import com.kiologyn.expenda.toSeconds
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DatePeriodSelectorContainer(
    fromDate: MutableState<LocalDateTime> = remember { mutableStateOf(LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) },
    toDate: MutableState<LocalDateTime> = remember { mutableStateOf(LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))) },
    height: Dp = 100.dp,
) {
    val PAGES_COUNT = 2
    val INITIAL_PAGE_INDEX = 0
    val CONTAINER_BORDER_RADUIS = height/3
    val CONTAINER_SHAPE = RoundedCornerShape(CONTAINER_BORDER_RADUIS, CONTAINER_BORDER_RADUIS)

    val pagerState = rememberPagerState(initialPage = INITIAL_PAGE_INDEX) { PAGES_COUNT }
    val selectedPageIndex by remember(pagerState.currentPage) { mutableIntStateOf(pagerState.currentPage) }
    var selectedTimeUnit by remember { mutableStateOf(DropdownMenuEnum.WEEK) }
    val datePeriodsStates: List<Map<String, MutableState<LocalDateTime>>> = listOf(
        mapOf(
            "from" to remember { mutableStateOf(LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) },
            "to" to remember { mutableStateOf(LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))) },
        ),
        mapOf(
            "from" to remember { mutableStateOf(LocalDateTime.now().minusWeeks(1).plusDays(1)) },
            "to" to remember { mutableStateOf(LocalDateTime.now()) },
        ),
    )

    LaunchedEffect(pagerState.settledPage) {
        fromDate.value = datePeriodsStates[pagerState.currentPage]["from"]!!.value
        toDate.value = datePeriodsStates[pagerState.currentPage]["to"]!!.value
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(
                LocalExpendaColors.current.periodPicker,
                CONTAINER_SHAPE,
            )
        ,
    ) {
        val CONTENT_PERCENT = 0.7f
        val CONTENT_INNER_PAGER_PERCENT = 0.675f
        val CONTENT_INNER_SPACE_PERCENT = 0.15f
        val CONTENT_INNER_DOTS_PERCENT = 0.175f

        val CONTENT_HEIGHT = height * CONTENT_PERCENT
        val PAGER_HEIGHT = CONTENT_HEIGHT * CONTENT_INNER_PAGER_PERCENT
        val SPACE_HEIGHT = CONTENT_HEIGHT * CONTENT_INNER_SPACE_PERCENT
        val DOTS_HEIGHT = CONTENT_HEIGHT * CONTENT_INNER_DOTS_PERCENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(height * (1-CONTENT_PERCENT)/2)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SPACE_HEIGHT),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CONTENT_HEIGHT/2))
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
                    0 -> TimePeriodSelectorElementContainer(PAGER_HEIGHT) {
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

                        val labelText by remember(localFromDate, localToDate) { mutableStateOf(
                            if (
                                localFromDate.toLocalDate().compareTo(
                                    LocalDate.now().with( when (selectedTimeUnit) {
                                        DropdownMenuEnum.WEEK -> TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                                        DropdownMenuEnum.MONTH -> TemporalAdjusters.firstDayOfMonth()
                                        DropdownMenuEnum.YEAR -> TemporalAdjusters.firstDayOfYear()
                                    } )
                                ) == 0
                            ) selectedTimeUnit.display
                            else when (selectedTimeUnit) {
                                DropdownMenuEnum.WEEK -> "${localFromDate.formatDate()} - ${localToDate.formatDate()}"
                                DropdownMenuEnum.MONTH -> "${localFromDate.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())} ${localFromDate.year}"
                                DropdownMenuEnum.YEAR -> "${localFromDate.year}"
                            }
                        ) }

                        val shiftDates: (Boolean) -> Unit = { isForward ->
                            val adjuster = when (selectedTimeUnit) {
                                DropdownMenuEnum.WEEK -> ChronoUnit.WEEKS
                                DropdownMenuEnum.MONTH -> ChronoUnit.MONTHS
                                DropdownMenuEnum.YEAR -> ChronoUnit.YEARS
                            }

                            localFromDate = if (isForward) localFromDate.plus(1, adjuster) else localFromDate.minus(1, adjuster)
                            localToDate = if (isForward) localToDate.plus(1, adjuster) else localToDate.minus(1, adjuster)

                            localToDate = localToDate.run { when (selectedTimeUnit) {
                                DropdownMenuEnum.MONTH -> with(TemporalAdjusters.lastDayOfMonth())
                                DropdownMenuEnum.YEAR -> with(TemporalAdjusters.lastDayOfYear())
                                else -> this
                            } }
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
                                    DropdownMenuEnum.entries.forEach { item ->
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
                    1 -> TimePeriodSelectorElementContainer(PAGER_HEIGHT) {
                        val fromDateText by remember(localFromDate) { mutableStateOf(localFromDate.formatDate()) }
                        val toDateText by remember(localToDate) { mutableStateOf(localToDate.formatDate()) }

                        var datePickerState by remember { mutableIntStateOf(0) }
                        val fromDatePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = localFromDate.toSeconds()*1000
                        )
                        val toDatePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = localToDate.toSeconds()*1000
                        )

                        if (datePickerState != 0)
                            DatePickerDialog(
                                onDismissRequest = { datePickerState = 0 },
                                confirmButton = {
                                    TextButton(onClick = {
                                        datePeriodStates[
                                            if (datePickerState == 1) "from" else "to"
                                        ]!!.value = (
                                            if (datePickerState == 1) fromDatePickerState else toDatePickerState
                                        ).selectedDateMillis!!.div(1000).toLocalDateTime()

                                        datePickerState = 0
                                    }) {
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
                                    .clickable { datePickerState = 1 }
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
                                    .clickable { datePickerState = 2 }
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
                size = DOTS_HEIGHT,
            )
        }
    }
}
@Composable
fun TimePeriodSelectorElementContainer(
    height: Dp = 50.dp,
    content: @Composable RowScope.() -> Unit = {},
) {
    val SHAPE = RoundedCornerShape(height*2)
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
        modifier = modifier.wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(size/3)
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
            LaunchedEffect(selectedIndex) {
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
        }
    }
}


@Preview
@Composable
private fun Preview() = ExpendaTheme {
    DatePeriodSelectorContainer()
}
