package com.kiologyn.expenda.ui

import com.kiologyn.expenda.ui.page.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


data class Page (
    val name: String,
    val icon: @Composable () -> Unit,
    val page: @Composable () -> Unit,
)

val pages = listOf(
    Page(
        "Home",
        @Composable {
            Icon(
                Icons.Rounded.Home,
                "Home",
            )
        },
        { Home() },
    ),
    Page(
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
    Page(
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
val START_PAGE_INDEX = 1
var pageIndexState: MutableState<Int> = mutableIntStateOf(START_PAGE_INDEX)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    var navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        modifier = Modifier.fillMaxSize(),
    ) {it
        NavHost(
            navController,
            pages[START_PAGE_INDEX].name,
        ) {
            for (page in pages) composable(page.name) { (page.page)() }
        }
    }
}
@Composable
fun BottomNavBar(navController: NavController) {
    val rememberedPageIndexState by remember { pageIndexState }
    NavigationBar {
        pages.forEachIndexed { index, page ->
            NavigationBarItem(
                icon = page.icon,
                label = { if (page.name != "Add") Text(page.name) },
                selected = rememberedPageIndexState == index,
                onClick = {
                    if (pageIndexState.value != index) {
                        pageIndexState.value = index
                        navController.navigate(page.name)
                    }
                },
                alwaysShowLabel = false,
            )
        }
    }
}

@Composable
@Preview
fun NavigationPreview() = Navigation()