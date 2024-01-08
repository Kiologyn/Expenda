package com.kiologyn.expenda.ui.records

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.record.RecordWithSubcategoryNameWithDailyFirstFlag
import com.kiologyn.expenda.formatDateDMY
import com.kiologyn.expenda.toLocalDateTime
import com.kiologyn.expenda.toSeconds
import com.kiologyn.expenda.ui.record.AddActivity
import com.kiologyn.expenda.ui.record.EditActivity
import com.kiologyn.expenda.ui.record.RecordCard
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


class RecordsActivity : ComponentActivity() {
    private lateinit var lazyColumnState: LazyListState
    private lateinit var recordsIds: MutableState<List<Int>>
    private lateinit var refreshRecords: MutableState<Boolean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            lazyColumnState = rememberLazyListState()
            recordsIds = remember { mutableStateOf(emptyList()) }
            refreshRecords = remember { mutableStateOf(true) }
            LaunchedEffect(refreshRecords.value) {
                if (refreshRecords.value) {
                    recordsIds.value = emptyList()
                    recordsIds.value = ExpendaDatabase
                        .build(applicationContext)
                        .recordDao()
                        .getAllIdsDESC()
                    refreshRecords.value = false
                }
            }

            ExpendaTheme {
                Layout()
            }
        }
    }

    @Composable
    fun Layout() {
        Scaffold(
            topBar = {
                TopBar()
            },
            content = { padding: PaddingValues ->
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
                            Intent(this, AddActivity::class.java)
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                    )
                }
            }
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar() {
        TopAppBar(
            title = { Text("Records") },
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "close records list"
                    )
                }
            },
            actions = {
                if (recordsIds.value.isEmpty()) return@TopAppBar
                Row(
                    modifier = Modifier.padding(end = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var openDialog by remember { mutableStateOf(false) }
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = LocalDateTime.of(
                            LocalDate.now(),
                            LocalTime.MIN,
                        ).toSeconds()*1000,
                    )

                    IconButton(onClick = {
                        openDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "jump to date",
                        )
                    }

                    val coroutineScope = rememberCoroutineScope()
                    if (openDialog)
                        DatePickerDialog(
                            onDismissRequest = { openDialog = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        val selectedDailyRecordIndex = recordsIds.value.indexOf(
                                            ExpendaDatabase
                                                .build(applicationContext)
                                                .recordDao()
                                                .getIdOfDailyLast(
                                                    datePickerState.selectedDateMillis?.div(1000) ?: 0
                                                )
                                        )
                                        if (selectedDailyRecordIndex >= 0)
                                            lazyColumnState.scrollToItem(
                                                selectedDailyRecordIndex
                                            )
                                        else
                                            Toast.makeText(
                                                applicationContext,
                                                "not found",
                                                Toast.LENGTH_SHORT,
                                            ).show()

                                        openDialog = false
                                    }
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    openDialog = false
                                }) {
                                    Text("Cancel")
                                }
                            },
                        ) {
                            DatePicker(state = datePickerState)
                        }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ContentList() {
        val pullRefreshState = rememberPullRefreshState(refreshRecords.value, { refreshRecords.value = true })
        Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
            if (recordsIds.value.isEmpty()) {
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
                    modifier = Modifier
                        .fillMaxSize()
                    ,
                    state = lazyColumnState,
                ) {
                    items(recordsIds.value) { recordId ->
                        var record by remember {
                            mutableStateOf<RecordWithSubcategoryNameWithDailyFirstFlag?>(null)
                        }
                        LaunchedEffect(true) {
                            record = ExpendaDatabase
                                .build(applicationContext)
                                .recordDao()
                                .getByIdWithSubcategoryAndDailyFirstFlag(recordId)
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
                                text = record!!.datetime.toLocalDateTime().formatDateDMY(),
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            )
                        RecordCard(
                            category = record?.subcategoryName,
                            amount = record?.amount,
                            datetime = record?.datetime?.toLocalDateTime(),
                            onClick = {
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        EditActivity::class.java,
                                    ).apply {
                                        putExtra(EditActivity.RECORD_ID_EXTRA_NAME, recordId)
                                    }
                                )
                            },
                        )
                    }
                }
            }
            PullRefreshIndicator(refreshRecords.value, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}
