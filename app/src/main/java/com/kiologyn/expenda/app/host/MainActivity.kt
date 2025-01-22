package com.kiologyn.expenda.app.host

import androidx.compose.runtime.Composable
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaActivity
import com.kiologyn.expenda.presentation.ui.main.MainScreen


class MainActivity : ExpendaActivity() {
    @Composable
    override fun Layout() {
        MainScreen()
    }
}
