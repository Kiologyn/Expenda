package com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.balance

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.R
import com.kiologyn.expenda.utils.adjustToNearestDay
import com.kiologyn.expenda.utils.format
import com.kiologyn.expenda.utils.toLocalDateTime
import com.kiologyn.expenda.utils.toSeconds
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.StatisticContainer
import java.time.LocalDate


internal class DailyBalanceTrendViewModel : ViewModel() {
    var points by mutableStateOf(listOf(Entry(0f, 0f)))
        private set

    suspend fun retrieveData(dateRange: ClosedRange<LocalDate>) {
        ExpendaApp.database.apply {
            points = recordDao().dailyBalanceRecordPerPeriod(
                dateRange.start.toSeconds(),
                dateRange.endInclusive.toSeconds(),
            ).map { Entry(it.date.toFloat(), it.balance.toFloat()) }
        }
    }
}

@Composable
fun DailyBalanceTrend(dateRange: ClosedRange<LocalDate>) {
    val ANIMATION_DURATION = 400

    val viewModel: DailyBalanceTrendViewModel = viewModel()

    StatisticContainer(title = "Daily balance trend") {
        LaunchedEffect(Unit) {
            viewModel.retrieveData(dateRange)
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1 / 0.75f)
            ,
            factory = { context ->
                LineChart(context).apply {
                    xAxis.apply {
                        labelCount = 5
                        valueFormatter = object : ValueFormatter() {
                            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                return value.toLong().toLocalDateTime().format(Helper.DATE_MY_FORMAT)
                            }
                        }
                        position = XAxis.XAxisPosition.BOTTOM
                        textColor = Color.WHITE
                        setDrawGridLines(false)
                    }
                    axisLeft.apply {
                        textColor = Color.WHITE
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
                            viewModel.points,
                            "balance",
                        ).apply {
                            mode = LineDataSet.Mode.HORIZONTAL_BEZIER

                            color = Color.parseColor("#CCCCCC")
                            lineWidth = 2f
                            setDrawCircles(false)

                            setDrawValues(false)
                            valueTextColor = Color.WHITE
                            valueTextSize = 16f

                            enableDashedHighlightLine(20f, 20f, 0f)
                            highlightLineWidth = 1f
                            highLightColor = Color.parseColor("#777777")
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

private class BalanceMarkerView(context: Context) : MarkerView(context, R.layout.balance_marker) {

    private val textView: TextView

    init {
        addView(TextView(context).apply {
            textView = this

            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.WHITE)
            textSize = 10f
            textAlignment = TEXT_ALIGNMENT_CENTER
        })
    }

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        if (entry == null) return

        val date = entry.x.toLong().toLocalDateTime().adjustToNearestDay()
        val balance = entry.y

        textView.text = "$balance\n${date.format(Helper.DATE_FORMAT)}"

        super.refreshContent(entry, highlight)
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        return MPPointF(-width/2f, -height*1.3f)
    }
}
