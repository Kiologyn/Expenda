package com.kiologyn.expenda.presentation.ui.main.page.statistics

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.DateRangePicker
import com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.DateRangePickerViewModel


@Composable
fun StatisticsPage() = Box(
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ,
) {
    val dateRangePickerViewModel: DateRangePickerViewModel = viewModel()
    val pickerHeight = 90.dp

    Statistics(
        dateRange = dateRangePickerViewModel.dateRange,
        pickerHeight = pickerHeight,
    )
    DateRangePicker(
        modifier = Modifier.align(Alignment.BottomCenter),
        viewModel = dateRangePickerViewModel,
        height = pickerHeight,
    )
}
