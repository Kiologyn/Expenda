package com.kiologyn.expenda.presentation.ui.main.page.home

import android.content.Intent
import android.icu.util.Currency
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.data.db.entity.Account
import com.kiologyn.expenda.data.db.dao.RecordWithSubcategoryName
import com.kiologyn.expenda.utils.toLocalDateTime
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaDotsPageIndicator
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors
import com.kiologyn.expenda.presentation.ui.accounts.AccountsActivity
import com.kiologyn.expenda.presentation.ui.record.AddRecordActivity
import com.kiologyn.expenda.presentation.ui.record.EditRecordActivity
import com.kiologyn.expenda.presentation.ui.record.RecordCard
import com.kiologyn.expenda.presentation.ui.records.RecordsActivity


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePage() {
    val localContext = LocalContext.current
    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                ,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    var accounts: List<Account> by remember { mutableStateOf(emptyList()) }
                    LaunchedEffect(Unit) {
                        ExpendaApp.database.apply {
                            accounts = accountDao().getAll()
                        }
                    }
                    val accountsPagerState = rememberPagerState(
                        pageCount = { accounts.size+1 },
                    )
                    val ACCOUNT_HEIGHT = 140.dp
                    HorizontalPager(state = accountsPagerState) {accountIndex -> when (accountIndex) {
                        in accounts.indices -> {
                            val account = accounts[accountIndex]
                            var balance by remember { mutableStateOf<Double?>(null) }

                            var refreshBalance by remember { mutableStateOf(true) }
                            LaunchedEffect(refreshBalance) {
                                if (refreshBalance) {
                                    balance = null
                                    ExpendaApp.database.apply {
                                        balance = recordDao().getBalanceByAccount(account.id)
                                    }
                                    refreshBalance = false
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(ACCOUNT_HEIGHT)
                                    .padding(vertical = 30.dp)
                                ,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Text(
                                    text = account.name,
                                    color = LocalExpendaColors.current.grayText,
                                )

                                TextButton(onClick = { refreshBalance = true }) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(0.5f),
                                        text = balance?.let{ balance ->
                                            listOf(
                                                "%.${Helper.ROUND_DECIMAL_PLACES}f".format(balance),
                                                Currency.getInstance(account.currencyCode).symbol,
                                            ).joinToString("")
                                        } ?: "•••",
                                        textAlign = TextAlign.Center,
                                        fontSize = 30.sp,
                                    )
                                }
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(ACCOUNT_HEIGHT)
                                ,
                                contentAlignment = Alignment.Center,
                            ) {
                                IconButton(
                                    modifier = Modifier.padding(50.dp),
                                    onClick = {
                                        localContext.startActivity(
                                            Intent(localContext, AccountsActivity::class.java)
                                        )
                                    },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(50.dp),
                                        imageVector = Icons.Default.Settings,
                                        tint = Color.Gray,
                                        contentDescription = "accounts settings",
                                    )
                                }
                            }
                        }
                    }}

                    if (accounts.isNotEmpty())
                        ExpendaDotsPageIndicator(
                            totalDots = accounts.size,
                            selectedIndex = accountsPagerState.currentPage,
                            selectedColor = LocalExpendaColors.current.dotIndicatorSelected,
                            unSelectedColor = LocalExpendaColors.current.dotIndicatorUnselected,
                            size = 10.dp,
                        )
                }

                RecordList(modifier = Modifier.weight(1f))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(10.dp),
                containerColor = LocalExpendaColors.current.surfaceContainerVariant,
                contentColor = LocalExpendaColors.current.surfaceContainer,
                onClick = {
                    localContext.startActivity(Intent(localContext, AddRecordActivity::class.java))
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
            ExpendaApp.database.apply {
                recordsList = recordDao().getAllWithSubcategoryNamesWithOffsetDESC(
                    offset = 0,
                    quantity = Helper.HOME_SCREEN_RECORDS_AMOUNT,
                )
            }
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
            Text(text = stringResource(R.string.home__see_all_records))
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(2.dp)
            ,
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
                        stringResource(R.string.home__records__empty_list),
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
                                account = record.accountName,
                                amount = record.amount,
                                datetime = record.datetime.toLocalDateTime(),
                                onClick = {
                                    localContext.startActivity(
                                        Intent(
                                            localContext,
                                            EditRecordActivity::class.java,
                                        ).apply {
                                            putExtra(EditRecordActivity.RECORD_ID_EXTRA_NAME, record.id)
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
                                        Text(stringResource(R.string.home__records__show_all), fontSize = MaterialTheme.typography.bodyLarge.fontSize)
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
