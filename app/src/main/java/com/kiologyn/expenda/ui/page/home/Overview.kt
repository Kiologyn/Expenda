package com.kiologyn.expenda.ui.page.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.kiologyn.expenda.Helper
import com.kiologyn.expenda.R
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.record.Record
import com.kiologyn.expenda.database.table.record.RecordWithSubcategoryName
import com.kiologyn.expenda.toLocalDateTime
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs


@Composable
fun Overview() {
    Box(Modifier.fillMaxSize()) {
        RecordList()
    }
}

@Composable
fun RecordList() {
    var recordsList by remember { mutableStateOf<List<RecordWithSubcategoryName>>(emptyList()) }

    val localContext = LocalContext.current
    LaunchedEffect(true) {
        val db = Room.databaseBuilder(
            localContext,
            ExpendaDatabase::class.java,
            Helper.DATABASE_NAME,
        ).build()

        recordsList = db.recordDao().getAllWithSubcategoryNamesDESC()
    }

    LazyColumn {
        items(recordsList) { record: RecordWithSubcategoryName ->
            RecordCard(
                category = record.subcategoryName,
                amount = record.amount,
                datetime = record.datetime.toLocalDateTime(),
            )
        }
    }
}

@Composable
fun RecordCard(
//    icon: Painter = painterResource(R.drawable.coin),
    category: String = "???",
    amount: Double = 0.toDouble(),
    datetime: LocalDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
//        leadingContent = {
//            val iconSize = 40.dp
//            Icon(
//                icon,
//                category,
//                modifier = Modifier
//                    .width(iconSize)
//                    .height(iconSize)
//            )
//        },
        headlineContent = { Text(category) },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    abs(amount).toString(),
                    color =
                        if (amount > 0) LocalExpendaColors.current.onSurfaceGreen
                        else if (amount < 0) LocalExpendaColors.current.onSurfaceRed
                        else LocalExpendaColors.current.grayText
                    ,
                    fontSize = 15.sp,
                )
                Text(
                    datetime.format(Helper.datetimeFormatter),
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
                amount = ((index-2)*13.412),
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