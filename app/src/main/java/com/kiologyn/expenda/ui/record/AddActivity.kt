package com.kiologyn.expenda.ui.record

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.record.Record
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.database.table.subcategory.Subcategory
import com.kiologyn.expenda.toSeconds
import com.kiologyn.expenda.ui.record.modify.RecordModify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class AddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    title = { Text("Add a record") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                finish()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    val dateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
                    val isIncomeState = remember { mutableStateOf(false) }
                    val amountState = remember { mutableStateOf(null as Double?) }
                    val subcategoryState = remember { mutableStateOf(null as Subcategory?) }
                    val descriptionState = remember { mutableStateOf("") }

                    val LINE_HEIGHT = 50.dp

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp, 50.dp)
                        ,
                    ) {
                        RecordModify(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                            ,
                            dateTimeState = dateTimeState,
                            isIncomeState = isIncomeState,
                            amountState = amountState,
                            subcategoryState = subcategoryState,
                            descriptionState = descriptionState,
                            lineHeight = LINE_HEIGHT,
                            activityResultRegistry = activityResultRegistry,
                        )

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(LINE_HEIGHT)
                            ,
                            onClick = {
                                if (
                                    amountState.value in listOf(null, 0.0) ||
                                    subcategoryState.value == null
                                ) return@Button // TODO: highlight inputs with red

                                val record = Record(
                                    datetime = dateTimeState.value.toSeconds(),
                                    amount = (if (isIncomeState.value) 1 else -1) * amountState.value!!,
                                    description = descriptionState.value.trim(' ', '\n'),
                                    idSubcategory = subcategoryState.value!!.id
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    ExpendaDatabase.build(applicationContext).apply {
                                        recordDao().insert(record)
                                    }.close()
                                }.invokeOnCompletion {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        finish()
                                    }
                                }
                            }
                        ) {
                            Text("Add", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
        )
    }


    @Preview
    @Composable
    private fun Preview() = ExpendaTheme { Layout() }
}
