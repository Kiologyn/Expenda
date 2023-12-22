package com.kiologyn.expenda

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kiologyn.expenda.ui.Navigation
import com.kiologyn.expenda.ui.theme.ExpendaTheme


class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Layout()
        }
    }

    @Composable
    private fun Layout() {
        ExpendaTheme {
            Navigation()
        }
    }

    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    private fun LayoutDarkPreview() = Layout()
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    private fun LayoutLightPreview() = Layout()
}
