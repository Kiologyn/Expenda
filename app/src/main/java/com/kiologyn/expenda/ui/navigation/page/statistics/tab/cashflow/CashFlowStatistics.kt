package com.kiologyn.expenda.ui.navigation.page.statistics.tab.cashflow

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.ExpendaApp
import com.kiologyn.expenda.round
import com.kiologyn.expenda.toSeconds
import com.kiologyn.expenda.ui.navigation.page.statistics.StatisticContainer
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.max



fun CashFlowStatistics(
    lazyListScope: LazyListScope,
    fromDate: MutableState<LocalDateTime>,
    toDate: MutableState<LocalDateTime>,
) {
    with(lazyListScope) {
        item { IncomeExpenseDifference(fromDate, toDate) }
    }
}

@Composable
private fun IncomeExpenseDifference(
    fromDate: MutableState<LocalDateTime>,
    toDate: MutableState<LocalDateTime>,
) {
    val ANIMATION_DURATION = 300

    StatisticContainer(title = "Cash flow") {
        var income by remember { mutableDoubleStateOf(0.0) }
        var expense by remember { mutableDoubleStateOf(0.0) }
        LaunchedEffect(fromDate.value, toDate.value) {
            ExpendaApp.database.apply {
                recordDao().cashFlow(
                    fromDate.value.toSeconds(),
                    toDate.value.toSeconds(),
                ).run {
                    income = this.income
                    expense = this.expense
                }
            }
        }
        val difference by remember(income, expense) { mutableDoubleStateOf(income - expense) }

        val incomeBarAnimatable by remember { mutableStateOf(Animatable(0f)) }
        LaunchedEffect(income) {
            incomeBarAnimatable.animateTo(
                targetValue = (income / max(income, expense)).let {
                    if (it.isNaN()) 0f else it.toFloat()
                },
                animationSpec = tween(
                    durationMillis = ANIMATION_DURATION,
                    easing = LinearEasing,
                )
            )
        }
        val expenseBarAnimatable by remember { mutableStateOf(Animatable(0f)) }
        LaunchedEffect(expense) {
            expenseBarAnimatable.animateTo(
                targetValue = (expense / max(income, expense)).let {
                    if (it.isNaN()) 0f else it.toFloat()
                },
                animationSpec = tween(
                    durationMillis = ANIMATION_DURATION,
                    easing = LinearEasing,
                )
            )
        }

        val BAR_HEIGHT = 30.dp
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = difference.round().let { (if (it < 0) "-" else "+") + abs(it).toString() },
                color =
                    if (difference < 0) LocalExpendaColors.current.onSurfaceRed
                    else if (difference > 0) LocalExpendaColors.current.onSurfaceGreen
                    else LocalExpendaColors.current.grayText
                ,
                style = MaterialTheme.typography.headlineSmall,
            )

            listOf(
                listOf("Income", income, incomeBarAnimatable.value, Color.Green),
                listOf("Expense", expense, expenseBarAnimatable.value, Color.Red),
            ).forEach { (name, value, valueBarAnimatable, color) ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                        ,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(name as String)
                        }
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Text((value as Double).round().toString())
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(valueBarAnimatable as Float)
                            .height(BAR_HEIGHT)
                            .background(
                                color as Color,
                                RoundedCornerShape(BAR_HEIGHT / 3),
                            )
                        ,
                    )
                }
            }
        }
    }
}
