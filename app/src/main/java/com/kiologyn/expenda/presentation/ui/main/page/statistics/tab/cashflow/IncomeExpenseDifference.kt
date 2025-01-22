package com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.cashflow

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.utils.round
import com.kiologyn.expenda.utils.toSeconds
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.StatisticContainer
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.max


internal class IncomeExpenseDifferenceViewModel : ViewModel() {
    var income by mutableDoubleStateOf(0.0)
    var expense by mutableDoubleStateOf(0.0)
    var difference by mutableDoubleStateOf(income - expense)

    private fun updateData(income: Double, expense: Double) {
        this.income = income
        this.expense = expense
        difference = income - expense
    }

    suspend fun retrieveData(dateRange: ClosedRange<LocalDate>) {
        ExpendaApp.database.apply {
            recordDao().cashFlow(
                dateRange.start.toSeconds(),
                dateRange.endInclusive.toSeconds(),
            ).also {
                updateData(it.income, it.expense)
            }
        }
    }
}

@Composable
fun IncomeExpenseDifference(dateRange: ClosedRange<LocalDate>) {
    val ANIMATION_DURATION = 300

    val viewModel: IncomeExpenseDifferenceViewModel = viewModel()

    StatisticContainer(title = "Cash flow") {
        LaunchedEffect(Unit) {
            viewModel.retrieveData(dateRange)
        }

        val incomeBarAnimatable by remember { mutableStateOf(Animatable(0f)) }
        LaunchedEffect(viewModel.income) {
            incomeBarAnimatable.animateTo(
                targetValue = (viewModel.income / max(viewModel.income, viewModel.expense)).let {
                    if (it.isNaN()) 0f else it.toFloat()
                },
                animationSpec = tween(
                    durationMillis = ANIMATION_DURATION,
                    easing = LinearEasing,
                )
            )
        }
        val expenseBarAnimatable by remember { mutableStateOf(Animatable(0f)) }
        LaunchedEffect(viewModel.expense) {
            expenseBarAnimatable.animateTo(
                targetValue = (viewModel.expense / max(viewModel.income, viewModel.expense)).let {
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
                text = viewModel.difference.round().let { (if (it < 0) "-" else "+") + abs(it).toString() },
                color =
                    if (viewModel.difference < 0) LocalExpendaColors.current.onSurfaceRed
                    else if (viewModel.difference > 0) LocalExpendaColors.current.onSurfaceGreen
                    else LocalExpendaColors.current.grayText
                ,
                style = MaterialTheme.typography.headlineSmall,
            )

            listOf(
                listOf("Income", viewModel.income, incomeBarAnimatable.value, Color.Green),
                listOf("Expense", viewModel.expense, expenseBarAnimatable.value, Color.Red),
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
