package com.kiologyn.expenda.presentation.ui.record.edit.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.presentation.theme.Black40


@Composable
fun AmountInput(
    modifier: Modifier = Modifier,
    amountState: MutableState<Double?> = remember { mutableStateOf(null) },
) {
    var text by remember { mutableStateOf(amountState.value?.toString() ?: "") }
    TextField(
        value = text,
        onValueChange = { value ->
            if (!value.contains(" ")) {
                val doubleValue = value.toDoubleOrNull()
                if (
                    (value == "" || doubleValue != null)
                    && value.substringAfter('.', " ".repeat(Helper.ROUND_DECIMAL_PLACES))
                        .length <= Helper.ROUND_DECIMAL_PLACES
                ) {
                    text = value
                    amountState.value = doubleValue
                }
            }
        },
        singleLine = true,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.record_edit__amount_placeholder), color = MaterialTheme.colorScheme.onSurfaceVariant) },
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
            focusedContainerColor = Black40,
        )
    )
}
