package com.kiologyn.expenda.ui.navigation.page.statistics.tab.balancetrend

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.record.DailyBalanceRecord
import com.kiologyn.expenda.formatDateMY
import com.kiologyn.expenda.toLocalDateTime
import com.kiologyn.expenda.toSeconds
import com.kiologyn.expenda.ui.navigation.page.statistics.StatisticContainer
import java.time.LocalDateTime


fun BalanceTrendStatistics(
    lazyListScope: LazyListScope,
    fromDate: MutableState<LocalDateTime>,
    toDate: MutableState<LocalDateTime>,
) {
    with(lazyListScope) {
        item { DailyBalanceTrend(fromDate, toDate) }
    }
}

@Composable
private fun DailyBalanceTrend(
    fromDate: MutableState<LocalDateTime>,
    toDate: MutableState<LocalDateTime>,
) {
    val localContext = LocalContext.current
    val ANIMATION_DURATION = 400

    StatisticContainer(title = "Daily balance trend") {
        var points by remember { mutableStateOf(listOf(
            DailyBalanceRecord(0, 0.0)
        )) }

        LaunchedEffect(fromDate.value, toDate.value) {
            points = ExpendaDatabase
                .build(localContext)
                .recordDao()
                .dailyBalanceRecordPerPeriod(
                    fromDate.value.toSeconds(),
                    toDate.value.toSeconds(),
                )
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
            ,
            factory = { context ->
                LineChart(context).apply {
                    xAxis.apply {
                        labelCount = 5
                        valueFormatter = object : ValueFormatter() {
                            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                return value.toLong().toLocalDateTime().formatDateMY()
                            }
                        }
                        position = XAxis.XAxisPosition.BOTTOM
                        textColor = AndroidColor.WHITE
                        setDrawGridLines(false)
                    }
                    axisLeft.apply {
                        textColor = AndroidColor.WHITE
                        enableGridDashedLine(20f, 10f, 0f)
                    }
                    legend.isEnabled = false // remove legend
                    description.isEnabled = false // remove description label
                    axisRight.isEnabled = false // remove right axis
                    marker = BalanceMarkerView(context) // set marker after touching
                    isDoubleTapToZoomEnabled = false
                    isScaleYEnabled = false // without y scaling
                }
            },
            update = { chart ->
                chart.apply {
                    resetZoom()
                    highlightValue(null)

                    data = LineData(
                        LineDataSet(
                            points.map {
                                Entry(it.date.toFloat(), it.balance.toFloat())
                            },
                            "balance",
                        ).apply {
                            mode = LineDataSet.Mode.HORIZONTAL_BEZIER

                            color = AndroidColor.parseColor("#CCCCCC")
                            lineWidth = 2f
                            setDrawCircles(false)

                            setDrawValues(false)
                            valueTextColor = AndroidColor.WHITE
                            valueTextSize = 16f

                            enableDashedHighlightLine(20f, 20f, 0f)
                            highlightLineWidth = 1f
                            highLightColor = AndroidColor.parseColor("#777777")
                            setDrawHorizontalHighlightIndicator(false)
                        }
                    )

                    animateXY(ANIMATION_DURATION, ANIMATION_DURATION)

                    invalidate()
                }
            },
        )
    }
}
