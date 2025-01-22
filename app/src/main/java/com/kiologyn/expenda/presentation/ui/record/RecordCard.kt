package com.kiologyn.expenda.presentation.ui.record

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
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.utils.format
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors
import java.time.LocalDateTime
import kotlin.math.abs


@Composable
fun RecordCard(
    category: String? = null,
    account: String? = null,
    amount: Double? = null,
    datetime: LocalDateTime? = null,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        modifier =
            if (onClick == null) Modifier
            else Modifier.clickable(onClick = onClick)
        ,
        headlineContent = { Text(category ?: "???") },
        supportingContent = { Text(
            text = account ?: "???",
            color = LocalExpendaColors.current.grayText,
        ) },
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
                    datetime?.format(Helper.DATETIME_FORMAT) ?: "???",
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
