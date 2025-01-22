package com.kiologyn.expenda.presentation.ui.main.page

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.kiologyn.expenda.R
import com.kiologyn.expenda.presentation.ui.main.page.home.HomePage
import com.kiologyn.expenda.presentation.ui.main.page.statistics.StatisticsPage


enum class MainScreenPage(
    val displayName: String,
    val icon: @Composable () -> Unit,
    val page: @Composable () -> Unit,
) {
    HOME(
        "Home",
        {
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = "Home page",
            )
        },
        { HomePage() },
    ),
    STATISTICS(
        "Statistics",
        {
            Icon(
                painter = painterResource(R.drawable.stats),
                contentDescription = "Statistics page",
            )
        },
        { StatisticsPage() },
    ),
}
