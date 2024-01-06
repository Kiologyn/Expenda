package com.kiologyn.expenda.ui.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.formatDateTime
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import java.time.LocalDateTime
import kotlin.math.abs


@Composable
fun RecordCard(
    category: String? = null,
    amount: Double? = null,
    datetime: LocalDateTime? = null,
    onClick: () -> Unit = {},
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(category ?: "???") },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    amount?.run { abs(this).toString() } ?: "???",
                    color =
                        if (amount == null || amount == 0.0) LocalExpendaColors.current.grayText
                        else if (amount > 0) LocalExpendaColors.current.onSurfaceGreen
                        else LocalExpendaColors.current.onSurfaceRed
                    ,
                    fontSize = 15.sp,
                )
                Text(
                    datetime?.formatDateTime() ?: "???",
                    color = LocalExpendaColors.current.grayText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
    )
}
