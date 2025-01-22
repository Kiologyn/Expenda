package com.kiologyn.expenda.presentation.ui.main.page.statistics.tab

import androidx.compose.foundation.lazy.LazyListScope
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.balance.DailyBalanceTrend
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.balance.BalanceByAccounts
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.balance.BalanceByCurrencies
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.cashflow.IncomeExpenseDifference
import com.kiologyn.expenda.presentation.ui.main.page.statistics.tab.categories.CategorySpending
import java.time.LocalDate


enum class StatisticsTab(
    val displayName: String,
    val content: LazyListScope.(dateRange: ClosedRange<LocalDate>) -> Unit,
) {
    BALANCE(
        "Balance",
        { dateRange ->
            item { DailyBalanceTrend(dateRange) }
            item { BalanceByAccounts(dateRange) }
            item { BalanceByCurrencies(dateRange) }
        }
    ),
    CATEGORIES(
        "Categories",
        { dateRange ->
            item { CategorySpending(dateRange) }
        }
    ),
    CASH_FLOW(
        "Cash flow",
        { dateRange ->
            item { IncomeExpenseDifference(dateRange) }
        }
    )
}
