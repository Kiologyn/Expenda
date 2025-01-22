package com.kiologyn.expenda.presentation.ui.main.page

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kiologyn.expenda.R
import com.kiologyn.expenda.presentation.ui.main.page.home.HomePage
import com.kiologyn.expenda.presentation.ui.main.page.statistics.StatisticsPage


enum class MainScreenPage(
    val displayName: @Composable () -> String,
    val icon: @Composable () -> Unit,
    val page: @Composable () -> Unit,
) {
    HOME(
        { stringResource(R.string.nav_bar__home) },
        {
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = "Home page",
            )
        },
        { HomePage() },
    ),
    STATISTICS(
        { stringResource(R.string.nav_bar__statistics) },
        {
            Icon(
                painter = painterResource(R.drawable.stats),
                contentDescription = "Statistics page",
            )
        },
        { StatisticsPage() },
    ),
}
