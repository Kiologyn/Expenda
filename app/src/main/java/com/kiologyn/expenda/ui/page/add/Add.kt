package com.kiologyn.expenda.ui.page.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.kiologyn.expenda.Helper
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.record.Record
import com.kiologyn.expenda.ui.theme.ExpendaTheme
import com.kiologyn.expenda.database.table.subcategory.Subcategory
import com.kiologyn.expenda.toMilliseconds
import com.kiologyn.expenda.ui.page.add.component.AmountInput
import com.kiologyn.expenda.ui.page.add.component.ArrowButton
import com.kiologyn.expenda.ui.page.add.component.DateTimePicker
import com.kiologyn.expenda.ui.page.add.component.DescriptionInput
import com.kiologyn.expenda.ui.page.add.component.categorypicker.CategoryPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun Add() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 50.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        val LINE_HEIGHT = 50.dp
        val ELEMENTS_BACKGROUND = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
        val BORDER_RADIUS = 15.dp

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
                    .background(ELEMENTS_BACKGROUND, RoundedCornerShape(BORDER_RADIUS))
                ,
                dateTimeState = dateTimeState,
            )

            Row(
                modifier = Modifier.height(LINE_HEIGHT),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ArrowButton(
                    modifier = Modifier
                        .background(ELEMENTS_BACKGROUND, shape = RoundedCornerShape(BORDER_RADIUS))
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
                    .background(ELEMENTS_BACKGROUND, RoundedCornerShape(BORDER_RADIUS))
                ,
                subcategoryState,
            )

            DescriptionInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LINE_HEIGHT*3)
                    .background(ELEMENTS_BACKGROUND, RoundedCornerShape(BORDER_RADIUS))
                ,
                descriptionState,
            )
        }

        val localContext = LocalContext.current
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
                    val db = Room.databaseBuilder(
                        localContext,
                        ExpendaDatabase::class.java,
                        Helper.DATABASE_NAME,
                    ).build()

                    db.recordDao().insert(record)
                }
            }
        ) {
            Text("Add", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}


@Preview
@Composable
fun AddPreview() = ExpendaTheme() { Add() }
//@Preview
//@Composable
//fun ArrowButtonPreview() = ExpendaTheme(true) { ArrowButton(onClick = {}) }