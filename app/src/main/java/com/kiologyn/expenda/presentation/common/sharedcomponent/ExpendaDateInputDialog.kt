package com.kiologyn.expenda.presentation.common.sharedcomponent

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
