package com.kiologyn.expenda.presentation.ui.savings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.utils.calculateSavingProfit
import com.kiologyn.expenda.utils.format
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun SavingCard(
    bank: String,
    accountName: String,
    depositAmount: Double,
    depositOpeningDate: LocalDate,
    depositClosingDate: LocalDate?,
    percent: Double,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(bank) },
        supportingContent = { Text(
            text = accountName,
            color = LocalExpendaColors.current.grayText,
        ) },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                val savingIncome = calculateSavingProfit(
                    startDate = depositOpeningDate,
                    endDate = depositClosingDate,
                    depositAmount = depositAmount,
                    percent = percent,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.White)) {
                            append(depositAmount.toString())
                        }
                        append(" ")
                        withStyle(SpanStyle(color = Color.Gray)) {
                            append("(")
                            withStyle(SpanStyle(color = Color.Green)) {
                                append("+$savingIncome")
                            }
                            append(")")
                        }
                    },
                    color = LocalExpendaColors.current.onSurfaceGreen,
                    fontSize = 15.sp,
                )
                Text(
                    text = listOf(
                        depositOpeningDate.format(Helper.DATE_FORMAT),
                        " - ",
                        depositClosingDate?.format(Helper.DATE_FORMAT) ?: "???",
                    ).joinToString(""),
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