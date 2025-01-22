package com.kiologyn.expenda.presentation.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kiologyn.expenda.presentation.ui.main.page.MainScreenPage


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var currentPage by remember { mutableStateOf(MainScreenPage.STATISTICS) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            NavHost(
                modifier = Modifier.padding(padding),
                navController = navController,
                startDestination = currentPage.name,
            ) {
                for (page in MainScreenPage.entries)
                    composable(page.name) { page.page() }
            }
        },
        bottomBar = {
            NavigationBar {
                MainScreenPage.entries.forEach { page ->
                    NavigationBarItem(
                        icon = page.icon,
                        label = { Text(page.displayName()) },
                        selected = page == currentPage,
                        onClick = {
                            if (page != currentPage) {
                                currentPage = page
                                navController.popBackStack()
                                navController.navigate(page.name)
                            }
                        },
                    )
                }
            }
        },
    )
}
