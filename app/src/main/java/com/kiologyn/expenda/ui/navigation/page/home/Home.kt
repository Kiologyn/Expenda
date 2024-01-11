package com.kiologyn.expenda.ui.navigation.page.home

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.Helper
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.record.RecordWithSubcategoryName
import com.kiologyn.expenda.toLocalDateTime
import com.kiologyn.expenda.ui.record.AddActivity
import com.kiologyn.expenda.ui.record.EditActivity
import com.kiologyn.expenda.ui.record.RecordCard
import com.kiologyn.expenda.ui.records.RecordsActivity
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors


@Composable
fun Home() {
    val localContext = LocalContext.current
    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                ,
            ) {
                BalanceView()
                RecordList(modifier = Modifier.weight(1f))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(10.dp),
                containerColor = LocalExpendaColors.current.surfaceContainerVariant,
                contentColor = LocalExpendaColors.current.surfaceContainer,
                onClick = {
                    localContext.startActivity(Intent(localContext, AddActivity::class.java))
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
private fun BalanceView(
    modifier: Modifier = Modifier,
) {
    var balanceValue by remember { mutableStateOf<Double?>(null) }

    val localContext = LocalContext.current
    var refreshBalance by remember { mutableStateOf(true) }
    LaunchedEffect(refreshBalance) {
        if (refreshBalance) {
            balanceValue = null
            balanceValue = ExpendaDatabase
                .build(localContext)
                .recordDao()
                .getBalance()
            refreshBalance = false
        }
    }

    Surface {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 30.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = "Total balance",
                color = LocalExpendaColors.current.grayText,
            )

            TextButton(onClick = { refreshBalance = true }) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    text = balanceValue?.let{ "%.${Helper.ROUND_DECIMAL_PLACES}f".format(it) } ?: "•••",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RecordList(
    modifier: Modifier = Modifier,
) {
    var recordsList by remember { mutableStateOf<List<RecordWithSubcategoryName>>(emptyList()) }

    val localContext = LocalContext.current
    var refresh by remember { mutableStateOf(true) }
    LaunchedEffect(refresh) {
        if (refresh) {
            recordsList = emptyList()
            recordsList = ExpendaDatabase
                .build(localContext)
                .recordDao()
                .getAllWithSubcategoryNamesWithOffsetDESC(
                    offset = 0,
                    quantity = Helper.HOME_SCREEN_RECORDS_AMOUNT,
                )
            refresh = false
        }
    }

    val BUTTON_HEIGHT = 50.dp
    val ELEMENT_SHAPE = RoundedCornerShape(BUTTON_HEIGHT/2, BUTTON_HEIGHT/2, 0.dp, 0.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ELEMENT_SHAPE)
            .background(LocalExpendaColors.current.surfaceContainer)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(BUTTON_HEIGHT)
                .clip(RoundedCornerShape(BUTTON_HEIGHT))
                .clickable {
                    localContext.startActivity(Intent(localContext, RecordsActivity::class.java))
                }
            ,
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "See All Records")
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(2.dp)
        )

        val pullRefreshState = rememberPullRefreshState(refresh, { refresh = true })
        Box(
            modifier = Modifier.pullRefresh(pullRefreshState),
            contentAlignment = Alignment.TopCenter,
        ) {
            if (recordsList.isEmpty() && !refresh) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            rememberScrollState(),
                            reverseScrolling = true,
                        )
                    ,
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Empty",
                        color = Color.Gray,
                        fontSize = 20.sp,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (!refresh) {
                        items(recordsList) { record: RecordWithSubcategoryName ->
                            RecordCard(
                                category = record.subcategoryName,
                                amount = record.amount,
                                datetime = record.datetime.toLocalDateTime(),
                                onClick = {
                                    localContext.startActivity(
                                        Intent(
                                            localContext,
                                            EditActivity::class.java,
                                        ).apply {
                                            putExtra(EditActivity.RECORD_ID_EXTRA_NAME, record.id)
                                        }
                                    )
                                },
                            )
                        }
                        if (recordsList.size == Helper.HOME_SCREEN_RECORDS_AMOUNT)
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                    ,
                                    contentAlignment = Alignment.Center,
                                ) {
                                    TextButton(
                                        modifier = Modifier
                                            .fillMaxWidth(0.3f)
                                        ,
                                        onClick = {
                                            localContext.startActivity(Intent(localContext, RecordsActivity::class.java))
                                        },
                                    ) {
                                        Text("Show all", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                                    }
                                }
                            }
                    }
                }
            }
            PullRefreshIndicator(refresh, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}


@Preview
@Composable
private fun Preview() = ExpendaTheme { Home() }