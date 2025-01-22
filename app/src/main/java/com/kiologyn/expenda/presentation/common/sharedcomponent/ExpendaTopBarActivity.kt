package com.kiologyn.expenda.presentation.common.sharedcomponent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

abstract class ExpendaTopBarActivity : ExpendaActivity() {
    protected abstract val title: String
    protected abstract val actions: @Composable (RowScope.() -> Unit)

    @Composable
    override fun Layout() {
        Scaffold(
            topBar = { TopBar() },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Content()
                }
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar() {
        TopAppBar(
            title = { Text(this.title) },
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close page",
                    )
                }
            },
            actions = this.actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), // TODO: redesign colors
        )
    }

    @Composable
    protected abstract fun Content()
}