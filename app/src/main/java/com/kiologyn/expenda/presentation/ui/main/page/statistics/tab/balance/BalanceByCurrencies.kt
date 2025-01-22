package com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.balance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.data.db.dao.CurrencyWithBalance
import com.kiologyn.expenda.utils.round
import com.kiologyn.expenda.utils.toSeconds
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.StatisticContainer
import java.time.LocalDate


@Composable
fun BalanceByCurrencies(dateRange: ClosedRange<LocalDate>) {
    var currencies: List<CurrencyWithBalance> by remember { mutableStateOf(emptyList()) }
    val currenciesBalancesSum: Double by remember(currencies) {
        mutableDoubleStateOf(currencies.let { it ->
            if (it.isEmpty()) 0.0
            else currencies.sumOf { it.balance }
        })
    }

    StatisticContainer(title = "Balance by currencies") {
        LaunchedEffect(Unit) {
            ExpendaApp.database.apply {
                currencies = accountDao().getAllCurrenciesWithBalancesPerPeriod(
                    fromDate = dateRange.start.toSeconds(),
                    toDate = dateRange.endInclusive.toSeconds(),
                )
            }
        }

        val BAR_HEIGHT = 30.dp
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            currencies.forEach { currency ->
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
                            Text(currency.code)
                        }
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Text(currency.balance.round().toString())
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((
                                if (currenciesBalancesSum == 0.0) 0.0
                                else currency.balance/currenciesBalancesSum
                            ).toFloat())
                            .height(BAR_HEIGHT)
                            .background(
                                Color.White,
                                RoundedCornerShape(BAR_HEIGHT / 3),
                            )
                        ,
                    )
                }
            }
        }
    }
}