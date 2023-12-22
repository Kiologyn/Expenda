package com.kiologyn.expenda.ui.page

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.ui.theme.Black30
import com.kiologyn.expenda.ui.theme.Black40
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import kotlinx.coroutines.launch

import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import com.kiologyn.expenda.Helper
import com.kiologyn.expenda.formatDate
import com.kiologyn.expenda.formatTime
import com.kiologyn.expenda.toLocalDateTime
import com.kiologyn.expenda.toMilliseconds
import java.time.LocalDateTime

@Composable
fun Add() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 50.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        val LINE_HEIGHT = 50.dp

        val dateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
        val isIncomeState = rememberSaveable { mutableStateOf(false) }
        val amountState = rememberSaveable { mutableDoubleStateOf(0.toDouble()) }

        DateTimePicker(
            modifier = Modifier.height(LINE_HEIGHT),
            dateTimeState = dateTimeState,
        )

        Row(
            modifier = Modifier.height(LINE_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ArrowButton(size = LINE_HEIGHT, typeState = isIncomeState, onClick = { /*TODO*/ })
            AmountInput(
                modifier = Modifier
                    .weight(1f)
                    .height(LINE_HEIGHT)
                ,
                amountState = amountState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerContainer(
    modifier: Modifier = Modifier,
    picker: @Composable () -> Unit,
    pickerText: MutableState<String>,
    onConfirm: () -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(Black30, RoundedCornerShape(15.dp))
            .clickable(onClick = {
                openDialog = true
            })
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(pickerText.value)
        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
            ,
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = Color.Gray,
        )

        if (openDialog) {
            DatePickerDialog(
                onDismissRequest = {
                    openDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openDialog = false
                            onConfirm()
                        },
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                picker()
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
    val timePickerText = remember { mutableStateOf(
        dateTimeState.value.format(Helper.timeFormatter)
    ) }
    PickerContainer(
        modifier = modifier,
        picker = { TimePicker(state = timePickerState) },
        timePickerText,
        onConfirm = {
            dateTimeState.value = dateTimeState.value
                .withHour(timePickerState.hour)
                .withMinute(timePickerState.minute)
            timePickerText.value = dateTimeState.value.formatTime()
        },
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
        picker = { DatePicker(state = datePickerState) },
        datePickerText,
        onConfirm = {
            dateTimeState.value = (datePickerState.selectedDateMillis ?: 0).toLocalDateTime()
            datePickerText.value = dateTimeState.value.formatDate()
        },
    )
}
@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    dateTimeState: MutableState<LocalDateTime> = remember { mutableStateOf(LocalDateTime.now()) },
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TimePickerElement(modifier.weight(1f), dateTimeState)
        DatePickerElement(modifier.weight(1f), dateTimeState)
    }
}

@Composable
fun ArrowButton(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    typeState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onClick: () -> Unit,
) {
    val SHADOW_SIZE = 0f
    val ARROW_SIZE = 0.50f
    val BORDER_RADIUS = 0.3f
    val ARROWS_SIZE = 0.2f
    val ARROWS_PADDING = 0.13f
    val ROTATION_TIME = 200

    val coroutineScope = rememberCoroutineScope()
    val arrowsRotationState by remember { mutableStateOf(Animatable(0f)) }
    var isArrowUp by typeState

    val arrowIcon: Painter =
        if (isArrowUp) painterResource(R.drawable.arrow_up)
        else painterResource(R.drawable.arrow_down)

    Box(
        modifier = modifier
            .shadow(size * SHADOW_SIZE, shape = RoundedCornerShape(size * BORDER_RADIUS))
            .background(Black30, shape = RoundedCornerShape(size * BORDER_RADIUS))
            .size(size)
            .clickable {
                isArrowUp = !isArrowUp
                coroutineScope.launch {
                    arrowsRotationState.snapTo(0f)
                    arrowsRotationState.animateTo(
                        targetValue = 180f,
                        animationSpec = tween(
                            durationMillis = ROTATION_TIME,
                            easing = LinearEasing,
                        ),
                    )
                }
                onClick()
            }
    ) {
        Image(
            arrowIcon,
            modifier = Modifier
                .align(Alignment.Center)
                .size(size * ARROW_SIZE)
            ,
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                if (isArrowUp) LocalExpendaColors.current.arrowGreen
                else LocalExpendaColors.current.arrowRed
            )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(size * ARROWS_SIZE + size * ARROWS_PADDING * 2)
            ,
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painterResource(R.drawable.arrows_reload),
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(arrowsRotationState.value)
                    .padding(size * ARROWS_PADDING)
                ,
                contentDescription = null,
                colorFilter = ColorFilter.tint(LocalExpendaColors.current.arrowsGray)
            )
        }
    }
}

@Composable
fun AmountInput(
    modifier: Modifier = Modifier,
    amountState: MutableDoubleState = remember { mutableDoubleStateOf(0.toDouble()) },
) {
    var text by remember { mutableStateOf("") }
    var amount by amountState
    TextField(
        value = text,
        onValueChange = { value ->
            if (!value.contains(" ")) {
                val doubleValue = value.toDoubleOrNull()
                if (value == "" || doubleValue != null) {
                    text = value
                    amount = doubleValue as Double
                }
            }
        },
        singleLine = true,
        modifier = modifier,
        placeholder = { Text("Amount", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            unfocusedContainerColor = Black30,
            focusedContainerColor = Black40,
        )
    )
}

@Preview
@Composable
fun AddPreview() = ExpendaTheme(true) { Add() }
//@Preview
//@Composable
//fun ArrowButtonPreview() = ExpendaTheme(true) { ArrowButton(onClick = {}) }