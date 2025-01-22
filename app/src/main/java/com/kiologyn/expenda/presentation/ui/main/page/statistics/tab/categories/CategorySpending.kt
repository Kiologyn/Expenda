package com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.R
import com.kiologyn.expenda.data.db.dao.CategoryExpense
import com.kiologyn.expenda.utils.toSeconds
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.StatisticContainer
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors
import java.time.LocalDate
import kotlin.math.pow


private class CategorySpendingViewModel : ViewModel() {

}

@Composable
fun CategorySpending(dateRange: ClosedRange<LocalDate>) {
    val ANIMATION_DURATION = 400

    var titleText by remember { mutableStateOf("Spending by categories") }
    StatisticContainer(title = titleText) {
        var allAmount by remember { mutableDoubleStateOf(0.toDouble()) }
        var pieData by remember { mutableStateOf<List<CategoryExpense>>(emptyList()) }
        var selectedCategory by remember { mutableStateOf<String?>(null) }
        var chosenCategory by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(chosenCategory) {
            ExpendaApp.database.apply {
                pieData = recordDao().run {
                    if (chosenCategory == null) categoriesExpenses(
                        dateRange.start.toSeconds(),
                        dateRange.endInclusive.toSeconds(),
                    )
                    else subcategoriesExpensesByCategory(
                        dateRange.start.toSeconds(),
                        dateRange.endInclusive.toSeconds(),
                        chosenCategory!!,
                    )
                }.sortedBy { -it.amount }
            }
            allAmount = pieData.sumOf { item -> item.amount }
        }

        Box(modifier = Modifier) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(vertical = 20.dp)
                ,
                factory = { context ->
                    PieChart(context).apply {
                        transparentCircleRadius = 0.1f
                        legend.isEnabled = false // remove legend
                        description.isEnabled = false // remove description label
                        dragDecelerationFrictionCoef = 0.95f
                    }
                },
                update = { chart ->
                    chart.apply {
                        highlightValue(null)

                        data = PieData(
                            PieDataSet(
                                pieData.map {
                                    PieEntry(it.amount.toFloat(), it.name)
                                },
                                "balance",
                            ).apply {
                                minAngleForSlices = 15f
                                setUsePercentValues(true)
                                setHoleColor(AndroidColor.TRANSPARENT)

                                centerText = "All\n%.${Helper.ROUND_DECIMAL_PLACES}f".format(allAmount)
                                setCenterTextColor(AndroidColor.WHITE)
                                setCenterTextSize(18f)

                                colors = ColorTemplate.MATERIAL_COLORS.toMutableList()

                                valueTextSize = 14f
                                valueTextColor = AndroidColor.WHITE
                                valueFormatter = object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String {
                                        val precision = 10.0.pow(Helper.ROUND_DECIMAL_PLACES.toDouble())
                                        return ((value * precision).toInt() / precision).toString()+"%"
                                    }
                                }

                                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                                    override fun onValueSelected(entry: Entry?, hightlight: Highlight?) {
                                        selectedCategory = entry?.let { (it as PieEntry).label }.toString()
                                    }

                                    override fun onNothingSelected() {
                                        selectedCategory = null
                                    }
                                })
                            }
                        )

                        animateY(ANIMATION_DURATION)

                        invalidate()
                    }
                },
            )


            val BUTTONS_SIZE = 45.dp
            val BUTTONS_PADDING = 10.dp
            val BUTTONS_SHAPE = RoundedCornerShape(BUTTONS_SIZE/2)
            val ARROWS_COLOR = Color.Gray

            // back button
            if (chosenCategory != null)
                Box(
                    modifier = Modifier
                        .size(BUTTONS_SIZE)
                        .clip(BUTTONS_SHAPE)
                        .clickable {
                            titleText = "Spending by categories"
                            selectedCategory = null
                            chosenCategory = null
                        }
                    ,
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(BUTTONS_PADDING)
                        ,
                        painter = painterResource(R.drawable.arrow_left_tight),
                        contentDescription = null,
                        tint = ARROWS_COLOR,
                    )
                }

            // forward button
            if (selectedCategory != null && chosenCategory == null)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(BUTTONS_SIZE)
                        .clip(BUTTONS_SHAPE)
                        .clickable {
                            titleText = "Spending by $selectedCategory subcategories"
                            chosenCategory = selectedCategory
                            selectedCategory = null
                        }
                    ,
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(BUTTONS_PADDING)
                        ,
                        painter = painterResource(R.drawable.arrow_right_tight),
                        contentDescription = null,
                        tint = ARROWS_COLOR,
                    )
                }
        }

        if (pieData.isNotEmpty())
            HorizontalDivider(color = Color(0xFF333333))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalExpendaColors.current.surfaceContainer)
            ,
        ) {
            pieData.forEach {
                CategoryExpenseCard(it.name, it.amount)
            }
        }
    }
}

@Composable
private fun CategoryExpenseCard(
    name: String = "None",
    amount: Double = 0.toDouble(),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        ,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 20.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(name, color = Color.White)
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 20.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            Text("%.${Helper.ROUND_DECIMAL_PLACES}f".format(amount), color = Color.White)
        }
    }
}
