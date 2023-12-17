package com.kiologyn.expenda.ui

import android.content.res.Configuration
import com.kiologyn.expenda.ui.page.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kiologyn.expenda.ui.page.home.Home


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
        "Add",
        @Composable {
            Icon(
                Icons.Rounded.Add,
                "Add",
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .fillMaxWidth(0.5f)
                ,
            )
        },
        { Add() },
    ),
    PageItem(
        "Account",
        @Composable {
            Icon(
                Icons.Rounded.AccountCircle,
                "Account",
            )
        },
        { Account() },
    ),
)
const val START_PAGE_INDEX = 1

@Composable
fun Navigation() {
    var navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),

        content = {padding ->
            NavHost(
                navController,
                pages[START_PAGE_INDEX].name,
                Modifier.padding(padding)
            ) {
                for (page in pages) composable(page.name) { (page.page)() }
            }
        },
        bottomBar = {
            var rememberedPageIndexState by remember { mutableIntStateOf(START_PAGE_INDEX) }
            NavigationBar {
                pages.forEachIndexed { index, page ->
                    NavigationBarItem(
                        icon = page.icon,
                        label = { if (page.name != "Add") Text(page.name) },
                        selected = rememberedPageIndexState == index,
                        onClick = {
                            if (rememberedPageIndexState != index) {
                                rememberedPageIndexState = index
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

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun NavigationPreview() = Navigation()