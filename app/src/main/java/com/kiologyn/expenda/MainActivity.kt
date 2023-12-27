package com.kiologyn.expenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultRegistry
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import com.kiologyn.expenda.ui.navigation.Navigation
import com.kiologyn.expenda.ui.theme.ExpendaTheme


class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpendaTheme {
                Layout()
            }
        }
    }

    @Composable
    private fun Layout() {
        Navigation()
    }

    @Preview
    @Composable
    private fun LayoutDarkPreview() = ExpendaTheme { Layout() }
}
