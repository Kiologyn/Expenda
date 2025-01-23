package com.kiologyn.expenda.presentation.ui.main.page.savings

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.data.db.dao.SavingWithAccount
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors
import com.kiologyn.expenda.presentation.ui.savings.SavingCard
import com.kiologyn.expenda.presentation.ui.savings.SavingEditActivity
import com.kiologyn.expenda.utils.toLocalDate

@Composable
fun SavingsPage() {
    val localContext = LocalContext.current
    Scaffold(
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                ,
            ) {
                SavingsList()
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(10.dp),
                containerColor = LocalExpendaColors.current.surfaceContainerVariant,
                contentColor = LocalExpendaColors.current.surfaceContainer,
                onClick = {
                    localContext.startActivity(Intent(localContext, SavingEditActivity::class.java))
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
fun SavingsList() {
    var savingsList by remember { mutableStateOf<List<SavingWithAccount>>(emptyList()) }
    
    val localContext = LocalContext.current
    var refresh by remember { mutableStateOf(true) }
    LaunchedEffect(refresh) {
        if (refresh) {
            savingsList = emptyList()
            ExpendaApp.database.apply {
                savingsList = savingDao().getAllWithAccountName()
            }
            refresh = false
        }
    }
    
    val pullRefreshState = rememberPullRefreshState(refresh, { refresh = true })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
        ,
        contentAlignment = Alignment.TopCenter,
    ) {
        if (savingsList.isEmpty() && !refresh) {
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
                    text = stringResource(R.string.savings__empty_list),
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
                    items(savingsList) { saving: SavingWithAccount ->
                        SavingCard(
                            bank = saving.bank,
                            accountName = saving.accountName,
                            depositAmount = saving.depositAmount,
                            depositOpeningDate = saving.depositOpeningDate.toLocalDate(),
                            depositClosingDate = saving.depositClosingDate?.toLocalDate(),
                            percent = saving.percent,
                            onClick = {
                                localContext.startActivity(
                                    Intent(
                                        localContext,
                                        SavingEditActivity::class.java,
                                    ).apply {
                                        putExtra(SavingEditActivity.SAVING_ID_EXTRA_NAME, saving.id)
                                    }
                                )
                            },
                        )
                    }
                }
            }
        }
        PullRefreshIndicator(refresh, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}
