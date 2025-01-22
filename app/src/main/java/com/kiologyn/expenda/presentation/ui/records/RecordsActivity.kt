package com.kiologyn.expenda.presentation.ui.records

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.kiologyn.expenda.R
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.data.db.dao.RecordWithSubcategoryNameWithDailyFirstFlag
import com.kiologyn.expenda.utils.format
import com.kiologyn.expenda.utils.toLocalDateTime
import com.kiologyn.expenda.utils.toSeconds
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaDateInputDialog
import com.kiologyn.expenda.presentation.common.sharedcomponent.PullRefreshBox
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaTopBarActivity
import com.kiologyn.expenda.presentation.ui.record.AddRecordActivity
import com.kiologyn.expenda.presentation.ui.record.EditRecordActivity
import com.kiologyn.expenda.presentation.ui.record.RecordCard
import com.kiologyn.expenda.presentation.theme.LocalExpendaColors
import kotlinx.coroutines.launch


class RecordsActivity : ExpendaTopBarActivity() {
    override val title: String
        get() = getString(R.string.records__title)
    override val actions: @Composable (RowScope.() -> Unit) = {
        if (recordsIdsState.value.isNotEmpty()) {
            var dialogOpened by remember { mutableStateOf(false) }
            ExpendaDateInputDialog(
                opened = dialogOpened,
                onConfirm = { selectedDate ->
                    if (selectedDate == null)
                        Helper.showToast(applicationContext, "error")
                    else
                        lifecycleScope.launch {
                            val selectedDailyRecordIndex = ExpendaApp.database.run {
                                recordsIdsState.value.indexOf(
                                    recordDao().getIdOfDailyLast(
                                        selectedDate.toSeconds().div(1000)
                                    )
                                )
                            }

                            if (selectedDailyRecordIndex >= 0)
                                lazyColumnState.scrollToItem(selectedDailyRecordIndex)
                            else
                                Helper.showToast(applicationContext, "not found")

                            dialogOpened = false
                        }
                },
                onDismiss = { dialogOpened = false },
            )

            IconButton(onClick = { dialogOpened = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "jump to date",
                )
            }
        }
    }


    private var lazyColumnState: LazyListState = LazyListState()
    private var recordsIdsState: MutableState<List<Int>> = mutableStateOf(emptyList())
    private var refreshingRecordsState: MutableState<Boolean> = mutableStateOf(true)

    @Composable
    override fun Content() {
        refreshingRecordsState = remember { mutableStateOf(refreshingRecordsState.value) }
        LaunchedEffect(refreshingRecordsState.value) {
            if (refreshingRecordsState.value) {
                ExpendaApp.database.apply {
                    recordsIdsState.value = recordDao().getAllIdsDESC()
                }
                refreshingRecordsState.value = false
            }
        }

        Scaffold(
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    ContentList()
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(10.dp),
                    containerColor = LocalExpendaColors.current.surfaceContainerVariant,
                    contentColor = LocalExpendaColors.current.surfaceContainer,
                    onClick = {
                        startActivity(
                            Intent(this, AddRecordActivity::class.java)
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                    )
                }
            }
        )
    }

    @Composable
    private fun ContentList() {
        PullRefreshBox(
            refreshing = refreshingRecordsState.value,
            onRefresh = { refreshingRecordsState.value = true }
        ) {
            if (recordsIdsState.value.isEmpty()) {
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
                        stringResource(R.string.records__empty_list),
                        color = Color.Gray,
                        fontSize = 20.sp,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyColumnState,
                ) {
                    items(recordsIdsState.value) { recordId ->
                        var record by remember {
                            mutableStateOf<RecordWithSubcategoryNameWithDailyFirstFlag?>(null)
                        }
                        LaunchedEffect(Unit) {
                            ExpendaApp.database.apply {
                                record = recordDao().getByIdWithSubcategoryAndDailyFirstFlag(
                                    recordId,
                                )
                            }
                        }

                        if (record?.isDailyFirst == true)
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 15.dp,
                                        vertical = 10.dp
                                    )
                                ,
                                text = record!!.datetime.toLocalDateTime().format(Helper.DATE_DMY_FORMAT),
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            )

                        RecordCard(
                            category = record?.subcategoryName,
                            account = record?.accountName,
                            amount = record?.amount,
                            datetime = record?.datetime?.toLocalDateTime(),
                            onClick = {
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        EditRecordActivity::class.java,
                                    ).apply {
                                        putExtra(EditRecordActivity.RECORD_ID_EXTRA_NAME, recordId)
                                    }
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
