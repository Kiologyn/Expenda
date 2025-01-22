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
import com.kiologyn.expenda.data.db.dao.AccountWithBalance
import com.kiologyn.expenda.utils.round
import com.kiologyn.expenda.utils.toSeconds
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.StatisticContainer
import java.time.LocalDate


@Composable
fun BalanceByAccounts(dateRange: ClosedRange<LocalDate>) {
    var accounts: List<AccountWithBalance> by remember { mutableStateOf(emptyList()) }
    val accountsBalancesSum: Double by remember(accounts) {
        mutableDoubleStateOf(accounts.let { it ->
            if (it.isEmpty()) 0.0
            else accounts.sumOf { it.balance }
        })
    }

    StatisticContainer(title = "Balance by accounts") {
        LaunchedEffect(Unit) {
            ExpendaApp.database.apply {
                accounts = accountDao().getAllWithBalancesPerPeriod(
                    fromDate = dateRange.start.toSeconds(),
                    toDate = dateRange.endInclusive.toSeconds(),
                )
            }
        }

        val BAR_HEIGHT = 30.dp
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            accounts.forEach { account ->
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
                            Text(account.name)
                        }
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Text(account.balance.round().toString())
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((
                                if (accountsBalancesSum == 0.0) 0.0
                                else account.balance/accountsBalancesSum
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