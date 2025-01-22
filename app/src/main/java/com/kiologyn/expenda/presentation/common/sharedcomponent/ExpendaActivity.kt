package com.kiologyn.expenda.presentation.common.sharedcomponent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.kiologyn.expenda.presentation.theme.ExpendaTheme

abstract class ExpendaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpendaTheme {
                Layout()
            }
        }
    }

    @Composable
    protected abstract fun Layout()
}