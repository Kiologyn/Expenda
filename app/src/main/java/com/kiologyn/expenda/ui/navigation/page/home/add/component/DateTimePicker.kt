package com.kiologyn.expenda.ui.navigation.page.home.add.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.formatDate
import com.kiologyn.expenda.formatTime
import com.kiologyn.expenda.toLocalDateTime
import com.kiologyn.expenda.toMilliseconds
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
    val timePickerText = remember { mutableStateOf(dateTimeState.value.formatTime()) }
    PickerContainer(
        modifier = modifier,
        picker = { openDialog ->
            DatePickerDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                            dateTimeState.value = dateTimeState.value
                                .withHour(timePickerState.hour)
                                .withMinute(timePickerState.minute)
                            timePickerText.value = dateTimeState.value.formatTime()
                        },
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                TimePicker(state = timePickerState)
            }
        },
        timePickerText,
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerElement(
    modifier: Modifier = Modifier,
    dateTimeState: MutableState<LocalDateTime> = remember { mutableStateOf(LocalDateTime.now()) },
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateTimeState.value.toMilliseconds(),
    )
    val datePickerText = remember { mutableStateOf(
        dateTimeState.value.formatDate()
    ) }
    PickerContainer(
        modifier = modifier,
        picker = { openDialog ->
            DatePickerDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                            dateTimeState.value = LocalDateTime.of(
                                (datePickerState.selectedDateMillis ?: 0).toLocalDateTime().toLocalDate(),
                                dateTimeState.value.toLocalTime(),
                            )
                            datePickerText.value = dateTimeState.value.formatDate()
                        },
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        },
        datePickerText,
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
