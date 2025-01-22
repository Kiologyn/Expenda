package com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors


@Composable
fun DateRangePickerPageContainer(content: @Composable BoxScope.() -> Unit) = Box(
    modifier = Modifier
        .fillMaxSize()
        .border(
            2.dp,
            LocalExpendaColors.current.dateRangePickerStroke,
            RoundedCornerShape(50),
        )
    ,
    contentAlignment = Alignment.Center,
    content = content,
)