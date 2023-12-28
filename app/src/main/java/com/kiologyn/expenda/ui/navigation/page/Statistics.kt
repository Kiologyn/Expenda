package com.kiologyn.expenda.ui.navigation.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors


@Composable
fun Statistics() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp)
            .background(MaterialTheme.colorScheme.background)
        ,
    ) {
        StatisticContainer(title = "Balance trend") {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp))
        }
        StatisticContainer(title = "Spending by categories") {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp))
        }
        StatisticContainer(title = "Cash flow") {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp))
        }
    }
}


@Composable
fun StatisticContainer(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit,
) {
    val OUTER_PADDING = PaddingValues(15.dp, 7.dp)
    val SHAPE = RoundedCornerShape(20.dp)
    val INNER_PADDING = PaddingValues(20.dp, 15.dp)

    Surface(modifier = Modifier.padding(OUTER_PADDING)) {
        Column(
            modifier = modifier
                .background(LocalExpendaColors.current.surfaceContainer, SHAPE)
                .padding(INNER_PADDING)
            ,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (title != null)
                Text(title, fontSize = 18.sp)
            content()
        }
    }
}


@Preview
@Composable
private fun Preview() = ExpendaTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) { Statistics() }
}
