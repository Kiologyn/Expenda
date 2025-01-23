package com.kiologyn.expenda.presentation.ui.savings.modify.component

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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.utils.format
import com.kiologyn.expenda.utils.toLocalDate
import com.kiologyn.expenda.utils.toSeconds
import java.time.LocalDate


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
fun DatePicker(
    modifier: Modifier = Modifier,
    date: LocalDate?,
    onPickDate: (LocalDate?) -> Unit,
    placeholder: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (date ?: LocalDate.now()).toSeconds() * 1000,
        )
        val datePickerText = remember(date) {
            mutableStateOf(date?.format(Helper.DATE_FORMAT) ?: placeholder)
        }
        PickerContainer(
            modifier = modifier.weight(1f),
            picker = { openDialog ->
                DatePickerDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            onPickDate(
                                datePickerState.selectedDateMillis?.let { it.div(1000).toLocalDate() }
                            )
                            openDialog.value = false
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
}
