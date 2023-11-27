package com.kiologyn.expenda

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kiologyn.expenda.ui.theme.ExpendaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Layout() }
    }

    @Composable
    private fun Layout() {
        ExpendaTheme {
            Surface (modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Expenda")
                }
            }
        }
    }

    /* preview block */
    @Preview(
        device = "spec:width=1080px,height=2340px,dpi=440,isRound=true",
        uiMode = Configuration.UI_MODE_NIGHT_YES,
    )
    @Composable
    private fun DarkLayout() {
        Layout()
    }
    @Preview(
        device = "spec:width=1080px,height=2340px,dpi=440,isRound=true",
        uiMode = Configuration.UI_MODE_NIGHT_NO,
    )
    @Composable
    private fun LightLayout() {
        Layout()
    }
}