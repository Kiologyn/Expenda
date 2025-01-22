package com.kiologyn.expenda.presentation.common.sharedcomponent

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kiologyn.expenda.R
import com.kiologyn.expenda.utils.toLocalDate
import com.kiologyn.expenda.utils.toSeconds
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpendaDateInputDialog(
    opened: Boolean,
    initialDate: LocalDate = LocalDate.now(),
    onConfirm: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toSeconds().times(1000L),
    )

    if (opened) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(datePickerState.selectedDateMillis?.run {
                        this.div(1000L).toLocalDate()
                    })
                }) {
                    Text(stringResource(R.string.dialog__ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.dialog__cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
