package com.kiologyn.expenda.ui.record

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.record.Record
import com.kiologyn.expenda.database.table.record.RecordWithSubcategory
import com.kiologyn.expenda.database.table.subcategory.Subcategory
import com.kiologyn.expenda.toLocalDateTime
import com.kiologyn.expenda.toSeconds
import com.kiologyn.expenda.ui.record.modify.RecordModify
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import kotlinx.coroutines.launch
import kotlin.math.abs


class EditActivity : ComponentActivity() {
    companion object {
        const val RECORD_ID_EXTRA_NAME = "recordId"
    }
    private var recordId = -1
    private var record: MutableState<RecordWithSubcategory?> = mutableStateOf(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordId = intent.getIntExtra(RECORD_ID_EXTRA_NAME, -1)
        if (recordId == -1) finish()

        setContent {
            ExpendaTheme {
                Layout()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Layout() {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            topBar = {
                TopAppBar(
                    title = { Text("Edit record") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                finish()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            },
            content = { Content(it) },
        )
    }

    @Composable
    private fun Content(padding: PaddingValues) {
        record = remember { mutableStateOf(null) }
        LaunchedEffect(true) {
            record.value = ExpendaDatabase
                .build(applicationContext)
                .recordDao()
                .getByIdWithSubcategory(recordId)
            if (record.value == null) finish()
        }
        if (record.value == null)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        else
            Box(modifier = Modifier.padding(padding)) {
                val dateTimeState = remember { mutableStateOf(record.value!!.datetime.toLocalDateTime()) }
                val isIncomeState = remember { mutableStateOf(record.value!!.amount >= 0) }
                val amountState = remember { mutableStateOf<Double?>(abs(record.value!!.amount)) }
                val subcategoryState = remember { mutableStateOf<Subcategory?>(
                    Subcategory(
                        id = record.value!!.subcategoryId,
                        name = record.value!!.subcategoryName,
                        idCategory = record.value!!.subcategoryCategoryId,
                    )
                ) }
                val descriptionState = remember { mutableStateOf(record.value!!.description) }

                val LINE_HEIGHT = 50.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp, 50.dp),
                ) {
                    RecordModify(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        dateTimeState = dateTimeState,
                        isIncomeState = isIncomeState,
                        amountState = amountState,
                        subcategoryState = subcategoryState,
                        descriptionState = descriptionState,
                        lineHeight = LINE_HEIGHT,
                        activityResultRegistry = activityResultRegistry,
                    )

                    val coroutineScope = rememberCoroutineScope()
                    var openDeleteDialog by remember { mutableStateOf(false) }
                    if (openDeleteDialog)
                        Dialog(
                            onDismissRequest = { openDeleteDialog = false },
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                        ) {
                            Surface(
                                modifier = Modifier
                                    .width(IntrinsicSize.Min)
                                    .height(IntrinsicSize.Min)
                                    .background(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = MaterialTheme.shapes.extraLarge,
                                    )
                                ,
                                shape = MaterialTheme.shapes.extraLarge,
                                tonalElevation = 6.dp,
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(20.dp),
                                ) {
                                    Text(
                                        text = "Delete confirmation",
                                        style = MaterialTheme.typography.labelMedium,
                                    )

                                    Text(
                                        text = "Are you sure?",
                                        style = MaterialTheme.typography.labelLarge,
                                    )

                                    Row(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .padding(top = 5.dp)
                                            .fillMaxWidth()
                                        ,
                                        horizontalArrangement = Arrangement.End,
                                    ) {
                                        TextButton(onClick = {
                                            openDeleteDialog = false
                                        }) {
                                            Text("No")
                                        }
                                        TextButton(onClick = {
                                            coroutineScope.launch {
                                                ExpendaDatabase
                                                    .build(applicationContext)
                                                    .recordDao()
                                                    .deleteById(record.value!!.id)
                                            }.invokeOnCompletion {
                                                finish()
                                            }
                                        }) {
                                            Text("OK")
                                        }
                                    }
                                }
                            }
                        }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .height(LINE_HEIGHT)
                                .aspectRatio(1f)
                            ,
                            onClick = { openDeleteDialog = true },
                            colors = IconButtonDefaults.outlinedIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                            )
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(LINE_HEIGHT)
                            ,
                            onClick = {
                                if (
                                    amountState.value == null ||
                                    subcategoryState.value == null
                                ) return@Button

                                val record = Record(
                                    id = record.value!!.id,
                                    datetime = dateTimeState.value.toSeconds(),
                                    amount = (if (isIncomeState.value) 1 else -1) * amountState.value!!,
                                    description = descriptionState.value,
                                    idSubcategory = subcategoryState.value!!.id
                                )
                                coroutineScope.launch {
                                    ExpendaDatabase
                                        .build(applicationContext)
                                        .recordDao()
                                        .upsert(record)
                                }.invokeOnCompletion {
                                    finish()
                                }
                            }
                        ) {
                            Text("Save", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
    }

    @Preview
    @Composable
    private fun Preview() = ExpendaTheme { Layout() }
}
