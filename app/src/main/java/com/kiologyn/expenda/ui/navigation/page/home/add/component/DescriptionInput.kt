package com.kiologyn.expenda.ui.navigation.page.home.add.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.ui.theme.Black40


@Composable
fun DescriptionInput(
    modifier: Modifier = Modifier,
    descriptionState: MutableState<String> = remember { mutableStateOf("") }
) {
    TextField(
        value = descriptionState.value,
        onValueChange = { value -> descriptionState.value = value },
        singleLine = false,
        modifier = modifier,
        placeholder = { Text("Description", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
            focusedContainerColor = Black40,
        )
    )
}
