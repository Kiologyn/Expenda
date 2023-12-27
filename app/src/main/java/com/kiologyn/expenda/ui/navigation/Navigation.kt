package com.kiologyn.expenda.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kiologyn.expenda.ui.navigation.page.Statistics
import com.kiologyn.expenda.ui.navigation.page.home.Home
import com.kiologyn.expenda.ui.theme.ExpendaTheme



private data class PageItem (
    val name: String,
    val icon: @Composable () -> Unit,
    val page: @Composable () -> Unit,
)

private val pages: List<PageItem> = listOf(
    PageItem(
        "Home",
        @Composable {
            Icon(
                Icons.Rounded.Home,
                "Home",
            )
        },
        { Home() },
    ),
    PageItem(
        "Statistics",
        @Composable {
            Icon(
                Icons.Rounded.Info,
                "Home",
            )
        },
        { Statistics() },
    ),
)
const val START_PAGE_INDEX = 0

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val rememberedPageIndexState = remember { mutableIntStateOf(START_PAGE_INDEX) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),

        content = {padding ->
            NavHost(
                modifier = Modifier.padding(padding),
                navController = navController,
                startDestination = pages[START_PAGE_INDEX].name,
            ) {
                for (page in pages) composable(page.name) { page.page() }
            }
        },
        bottomBar = {
            NavigationBar {
                pages.forEachIndexed { index, page ->
                    NavigationBarItem(
                        icon = page.icon,
                        label = { Text(page.name) },
                        selected = rememberedPageIndexState.intValue == index,
                        onClick = {
                            if (rememberedPageIndexState.intValue != index) {
                                rememberedPageIndexState.intValue = index
                                navController.popBackStack()
                                navController.navigate(page.name)
                            }
                        },
                        alwaysShowLabel = false,
                    )
                }
            }
        },
    )
}

@Preview
@Composable
fun NavigationPreview() = ExpendaTheme(true) { Navigation() }