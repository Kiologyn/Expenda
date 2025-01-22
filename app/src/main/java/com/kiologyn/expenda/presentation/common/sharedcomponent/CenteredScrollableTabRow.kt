package com.kiologyn.expenda.presentation.common.sharedcomponent

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.max


@Composable
fun CenteredScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
        TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]))
    },
    divider: @Composable () -> Unit = { },
    onTabClick: (tabIndex: Int) -> Unit,
    tabs: List<String> = emptyList(),
) {
    val density = LocalDensity.current.density
    var fullWidthValue by remember { mutableFloatStateOf(0f) }
    var innerTabsWidth by remember { mutableStateOf(List(tabs.size) { 0 }) }
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.onSizeChanged { fullWidthValue = it.width.toFloat() },
        containerColor = containerColor,
        contentColor = contentColor,
        indicator = indicator,
        edgePadding = max(0f, (fullWidthValue - innerTabsWidth.sum()) / 2 / density).dp,
        divider = divider,
    ) {
        tabs.onEachIndexed { index, tabName ->
            Tab(
                modifier = Modifier.onSizeChanged {
                    innerTabsWidth = innerTabsWidth.toMutableList().apply {
                        this[index] = it.width
                    }.toList()
                },
                text = { Text(tabName) },
                selected = selectedTabIndex == index,
                onClick = { onTabClick(index) },
            )
        }
    }
}