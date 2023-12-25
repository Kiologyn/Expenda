package com.kiologyn.expenda.ui.page.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.kiologyn.expenda.Helper
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.record.RecordWithSubcategoryName
import com.kiologyn.expenda.toLocalDateTime
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs


@Composable
fun Overview() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BalanceView()
        RecordList(modifier = Modifier.weight(1f))
    }
}

@Composable
fun BalanceView(
    modifier: Modifier = Modifier,
) {
    var balanceValue by remember { mutableStateOf<Int?>(null) }

    val localContext = LocalContext.current
    LaunchedEffect(true) {
        val db = Room.databaseBuilder(
            localContext,
            ExpendaDatabase::class.java,
            Helper.DATABASE_NAME,
        ).build()

        balanceValue = db.recordDao().getBalance()
    }

    Surface {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .shadow(0.dp, RoundedCornerShape(30.dp))
                .padding(50.dp, 20.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Total balance", color = LocalExpendaColors.current.grayText)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(40.dp, 5.dp)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(balanceValue?.toString() ?: "???", fontSize = 30.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecordList(
    modifier: Modifier = Modifier,
) {
    var recordsList by remember { mutableStateOf<List<RecordWithSubcategoryName>>(emptyList()) }

    val localContext = LocalContext.current
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true

        val db = Room.databaseBuilder(
            localContext,
            ExpendaDatabase::class.java,
            Helper.DATABASE_NAME,
        ).build()

        recordsList = db.recordDao().getAllWithSubcategoryNamesDESC()

        refreshing = false
    }
    LaunchedEffect(true) {
        refresh()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                LocalExpendaColors.current.surfaceContainer,
                shape = RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp)
            )
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.15f)
                .padding(top = 10.dp)
                .clickable {
                    // TODO: open activity with all records
                }
            ,
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "recordsList",
                tint = LocalExpendaColors.current.grayText,
            )
        }


        val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

        Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
            LazyColumn(Modifier.fillMaxSize()) {
                if (!refreshing) {
                    items(recordsList) { record: RecordWithSubcategoryName ->
                        RecordCard(
                            category = record.subcategoryName,
                            amount = record.amount,
                            datetime = record.datetime.toLocalDateTime(),
                        )
                    }
                }
            }
            PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
    }

}

@Composable
fun RecordCard(
//    icon: Painter = painterResource(R.drawable.coin),
    category: String? = null,
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
        headlineContent = { Text(category ?: "???") },
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
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
    )
}


@Composable
fun RecordListPreview() {
    LazyColumn(Modifier.background(LocalExpendaColors.current.surfaceContainer)) {
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
fun OverviewPreview() = ExpendaTheme { Overview() }
@Preview
@Composable
fun RecordListPreviewPreview() = ExpendaTheme { RecordListPreview() }
//@Preview
//@Composable
//fun RecordListPreviewDark() = ExpendaTheme { Overview() }