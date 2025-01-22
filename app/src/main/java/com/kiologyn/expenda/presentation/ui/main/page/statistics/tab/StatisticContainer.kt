package com.kiologyn.expenda.presentation.ui.main.page.statistics.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors


@Composable
fun StatisticContainer(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit,
) {
    val OUTER_PADDING = PaddingValues(10.dp)
    val SHAPE = RoundedCornerShape(20.dp)
    val INNER_PADDING = PaddingValues(20.dp, 15.dp)

    Surface(
        modifier = Modifier
            .padding(OUTER_PADDING)
            .fillMaxWidth()
        ,
    ) {
        Column(
            modifier = modifier
                .background(LocalExpendaColors.current.surfaceContainer, SHAPE)
                .padding(INNER_PADDING)
            ,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (title != null)
                Text(title, fontSize = 18.sp)

            content()
        }
    }
}
