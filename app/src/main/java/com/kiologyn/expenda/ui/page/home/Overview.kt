package com.kiologyn.expenda.ui.page.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
fun Overview() {
    Box(Modifier.fillMaxSize()) {
        RecordList()
    }
}

@Composable
fun RecordList() {
    LazyColumn {
        // TODO: retrieve records from db
    }
}

@Composable
fun RecordCard(
    icon: Painter = painterResource(R.drawable.coin),
    category: String = "???",
    amount: Float = 0f,
    date: LocalDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            val iconSize = 40.dp
            Icon(
                icon,
                category,
                modifier = Modifier
                    .width(iconSize)
                    .height(iconSize)
            )
        },
        headlineContent = { Text(category) },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    amount.toString(),
                    color =
                        if (amount >= 0) LocalExpendaColors.current.onSurfaceGreen
                        else LocalExpendaColors.current.onSurfaceRed,
                    fontSize = 15.sp,
                )
                Text(
                    date.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")),
                    color = LocalExpendaColors.current.grayText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        },
    )
    Divider()
}


@Composable
fun RecordListPreview(darkMode: Boolean) = ExpendaTheme(darkMode) {
    LazyColumn {
        itemsIndexed(listOf(
            "Food",
            "Home",
            "Clothes",
            "Loan",
            "Other",
        )) {index, category ->
            RecordCard(
                category = category,
                amount = ((index-2)*13.412).toFloat(),
            )
        }
    }
}
@Preview
@Composable
fun RecordListPreviewDark() = RecordListPreview(true)
@Preview
@Composable
fun RecordListPreviewLight() = RecordListPreview(false)