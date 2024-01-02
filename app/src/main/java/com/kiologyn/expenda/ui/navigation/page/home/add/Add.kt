package com.kiologyn.expenda.ui.navigation.page.home.add

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultRegistry
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.record.Record
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.database.table.subcategory.Subcategory
import com.kiologyn.expenda.toMilliseconds
import com.kiologyn.expenda.ui.navigation.page.home.add.component.AmountInput
import com.kiologyn.expenda.ui.navigation.page.home.add.component.ArrowButton
import com.kiologyn.expenda.ui.navigation.page.home.add.component.DateTimePicker
import com.kiologyn.expenda.ui.navigation.page.home.add.component.DescriptionInput
import com.kiologyn.expenda.ui.navigation.page.home.add.component.categorypicker.CategoryPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime


val LocalActivityResultRegistry = compositionLocalOf<ActivityResultRegistry?> { null }

class AddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalActivityResultRegistry provides activityResultRegistry
            ) {
                ExpendaTheme {
                    Layout()
                }
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
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp, 50.dp)
                        ,
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        val LINE_HEIGHT = 50.dp
                        val ELEMENTS_BACKGROUND = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                        val SHAPE = RoundedCornerShape(15.dp)

                        val dateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
                        val isIncomeState = remember { mutableStateOf(false) }
                        val amountState = remember { mutableStateOf(null as Double?) }
                        val subcategoryState = remember { mutableStateOf(null as Subcategory?) }
                        val descriptionState = remember { mutableStateOf("") }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                            ,
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            DateTimePicker(
                                modifier = Modifier
                                    .height(LINE_HEIGHT)
                                    .background(ELEMENTS_BACKGROUND, SHAPE)
                                    .clip(SHAPE)
                                ,
                                dateTimeState = dateTimeState,
                            )

                            Row(
                                modifier = Modifier.height(LINE_HEIGHT),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ArrowButton(
                                    modifier = Modifier
                                        .background(ELEMENTS_BACKGROUND, SHAPE)
                                        .clip(SHAPE)
                                    ,
                                    size = LINE_HEIGHT,
                                    isArrowUp = isIncomeState,
                                )
                                AmountInput(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(LINE_HEIGHT),
                                    amountState = amountState,
                                )
                            }

                            CategoryPicker(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(LINE_HEIGHT)
                                    .background(ELEMENTS_BACKGROUND, SHAPE)
                                    .clip(SHAPE)
                                ,
                                subcategoryState,
                            )

                            DescriptionInput(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(LINE_HEIGHT * 3)
                                    .background(ELEMENTS_BACKGROUND, SHAPE)
                                ,
                                descriptionState,
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
                                    datetime = dateTimeState.value.toMilliseconds(),
                                    amount = (if (isIncomeState.value) 1 else -1) * amountState.value!!,
                                    description = descriptionState.value,
                                    idSubcategory = subcategoryState.value!!.id
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    val db = ExpendaDatabase.build(applicationContext)
                                    db.recordDao().insert(record)

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
