package com.kiologyn.expenda.presentation.ui.record.modify.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kiologyn.expenda.R
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.utils.format
import com.kiologyn.expenda.utils.toLocalDateTime
import com.kiologyn.expenda.utils.toSeconds
import java.time.LocalDateTime


@Composable
fun PickerContainer(
    modifier: Modifier = Modifier,
    picker: @Composable (MutableState<Boolean>) -> Unit,
    pickerText: MutableState<String>,
) {
    val openDialog = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clickable { openDialog.value = true  }
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(pickerText.value, color = MaterialTheme.colorScheme.onSurface)
        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
            ,
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = Color.Gray,
        )

        if (openDialog.value)
            picker(openDialog)
    }
}
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = stringResource(R.string.time_picker_dialog__title),
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton()
                    confirmButton()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerElement(
    modifier: Modifier = Modifier,
    dateTimeState: MutableState<LocalDateTime> = remember { mutableStateOf(LocalDateTime.now()) },
) {
    val timePickerState = rememberTimePickerState(
        initialHour = dateTimeState.value.hour,
        initialMinute = dateTimeState.value.minute,
    )
    val timePickerText = remember(dateTimeState.value) {
        mutableStateOf(dateTimeState.value.format(Helper.TIME_FORMAT))
    }
    PickerContainer(
        modifier = modifier,
        picker = { openDialog ->
            TimePickerDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        openDialog.value = false
                        dateTimeState.value = dateTimeState.value
                            .withHour(timePickerState.hour)
                            .withMinute(timePickerState.minute)
                    }) {
                        Text(stringResource(R.string.dialog__ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        openDialog.value = false
                    }) {
                        Text(stringResource(R.string.dialog__cancel))
                    }
                }
            ) {
                TimePicker(state = timePickerState)
            }
        },
        pickerText = timePickerText,
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerElement(
    modifier: Modifier = Modifier,
    dateTimeState: MutableState<LocalDateTime> = remember { mutableStateOf(LocalDateTime.now()) },
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateTimeState.value.toSeconds()*1000,
    )
    val datePickerText = remember(dateTimeState.value) {
        mutableStateOf(dateTimeState.value.format(Helper.DATE_FORMAT))
    }
    PickerContainer(
        modifier = modifier,
        picker = { openDialog ->
            DatePickerDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        openDialog.value = false
                        dateTimeState.value = LocalDateTime.of(
                            (datePickerState.selectedDateMillis?.div(1000) ?: 0)
                                .toLocalDateTime().toLocalDate(),
                            dateTimeState.value.toLocalTime(),
                        )
                    }) {
                        Text(stringResource(R.string.dialog__ok))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Text(stringResource(R.string.dialog__cancel))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        },
        pickerText = datePickerText,
    )
}
@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    dateTimeState: MutableState<LocalDateTime> = remember { mutableStateOf(LocalDateTime.now()) },
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TimePickerElement(modifier.weight(1f), dateTimeState)
        DatePickerElement(modifier.weight(1f), dateTimeState)
    }
}
