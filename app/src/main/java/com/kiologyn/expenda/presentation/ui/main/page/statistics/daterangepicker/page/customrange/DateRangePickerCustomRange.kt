package com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page.customrange

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.utils.format
import com.kiologyn.expenda.presentation.common.sharedcomponent.DateRangeInputDialog
import com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page.DateRangePickerPageContainer
import java.time.LocalDate


@Composable
fun DateRangePickerPageCustomRange(
    dateRange: ClosedRange<LocalDate>,
    onPick: (newDateRange: ClosedRange<LocalDate>) -> Unit,
) = DateRangePickerPageContainer {
    val localContext = LocalContext.current
    var openDialog by remember { mutableStateOf(false) }
    DateRangeInputDialog(
        opened = openDialog,
        initialDateRange = dateRange,
        onConfirm = { dateRange ->
            if (dateRange == null)
                Helper.showToast(localContext, "error picking date range")
            else
                onPick(dateRange)

            openDialog = false
        },
        onDismiss = { openDialog = false },
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                openDialog = true
            }
        ,
        contentAlignment = Alignment.Center,
    ) {
        Text(listOf(
            dateRange.start.format(Helper.DATE_FORMAT),
            " - ",
            dateRange.endInclusive.format(Helper.DATE_FORMAT),
        ).joinToString("")) // TODO: change
    }
}