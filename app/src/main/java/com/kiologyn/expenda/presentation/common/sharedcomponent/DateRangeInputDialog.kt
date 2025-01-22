package com.kiologyn.expenda.presentation.common.sharedcomponent

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.utils.toLocalDate
import com.kiologyn.expenda.utils.toSeconds
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeInputDialog(
    opened: Boolean,
    initialDateRange: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now(),
    onConfirm: (ClosedRange<LocalDate>?) -> Unit,
    onDismiss: () -> Unit,
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialDateRange.start.toSeconds().times(1000),
        initialSelectedEndDateMillis = initialDateRange.endInclusive.toSeconds().times(1000),
    )

    if (opened) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val startDate = dateRangePickerState.selectedStartDateMillis?.let {
                        it.div(1000).toLocalDate()
                    }
                    val endDate = dateRangePickerState.selectedEndDateMillis?.let {
                        it.div(1000).toLocalDate()
                    }
                    onConfirm(if (startDate == null || endDate == null) null else startDate..endDate)
                }) {
                    Text(stringResource(R.string.dialog__ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.dialog__cancel))
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(16.dp),
                title = {
                    Text(stringResource(R.string.date_range_dialog__title))
                },
                showModeToggle = false,
            )
        }
    }
}