package com.kiologyn.expenda.ui.navigation.page.statistics.tab

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.MutableState
import com.kiologyn.expenda.ui.navigation.page.statistics.tab.balancetrend.BalanceTrendStatistics
import com.kiologyn.expenda.ui.navigation.page.statistics.tab.cashflow.CashFlowStatistics
import com.kiologyn.expenda.ui.navigation.page.statistics.tab.categoryspending.CategorySpendingStatistics
import java.time.LocalDateTime


data class TabItem(
    val name: String,
    val tab: (
        LazyListScope,
        MutableState<LocalDateTime>,
        MutableState<LocalDateTime>,
    ) -> Unit,
)

val tabs: List<TabItem> = listOf(
    TabItem(
        name = "Balance trend",
        tab = ::BalanceTrendStatistics,
    ),
    TabItem(
        name = "Categories",
        tab = ::CategorySpendingStatistics,
    ),
    TabItem(
        name = "Cash flow",
        tab = ::CashFlowStatistics,
    ),
)

const val START_TAB_INDEX = 0
