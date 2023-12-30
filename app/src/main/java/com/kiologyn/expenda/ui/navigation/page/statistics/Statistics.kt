package com.kiologyn.expenda.ui.navigation.page.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import java.time.LocalDateTime


@Composable
fun Statistics() {
    val fromDate = remember { mutableStateOf<LocalDateTime>(LocalDateTime.now().minusWeeks(1)) }
    val toDate = remember { mutableStateOf<LocalDateTime>(LocalDateTime.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
            .background(MaterialTheme.colorScheme.background)
        ,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BalanceTrendStatistic()
            CategoriesSpendingStatistic()
            CashFlowStatistic()
        }

        DatePeriodSelectorContainer(fromDate, toDate)
    }
}

@Composable
fun BalanceTrendStatistic() {
    StatisticContainer(title = "Balance trend") {

    }
}
@Composable
fun CategoriesSpendingStatistic() {
    StatisticContainer(title = "Spending by categories") {

    }
}
@Composable
fun CashFlowStatistic() {
    StatisticContainer(title = "Cash flow") {

    }
}


@Composable
fun StatisticContainer(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit,
) {
    val OUTER_PADDING = PaddingValues(15.dp, 7.dp)
    val SHAPE = RoundedCornerShape(20.dp)
    val INNER_PADDING = PaddingValues(20.dp, 15.dp)

    Surface(
        modifier = Modifier
            .padding(OUTER_PADDING)
            .fillMaxWidth()
        ,
    ) {
        Column(
            modifier = modifier
                .background(LocalExpendaColors.current.surfaceContainer, SHAPE)
                .padding(INNER_PADDING)
            ,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (title != null)
                Text(title, fontSize = 18.sp)

            content()
        }
    }
}


@Preview
@Composable
private fun Preview() = ExpendaTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) { Statistics() }
}
