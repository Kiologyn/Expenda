package com.kiologyn.expenda.presentation.ui.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.data.db.entity.Account
import com.kiologyn.expenda.data.db.entity.Record
import com.kiologyn.expenda.data.db.entity.Subcategory
import com.kiologyn.expenda.utils.toSeconds
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaTopBarActivity
import com.kiologyn.expenda.presentation.ui.record.edit.RecordModify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class AddRecordActivity : ExpendaTopBarActivity() {
    override val title: String
        get() = getString(R.string.record_edit__title_add)
    override val actions: @Composable() (RowScope.() -> Unit) = {}

    @Composable
    override fun Content() {
        val dateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
        val isIncomeState = remember { mutableStateOf(false) }
        val amountState = remember { mutableStateOf(null as Double?) }
        val subcategoryState = remember { mutableStateOf(null as Subcategory?) }
        val accountState = remember { mutableStateOf(null as Account?) }
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
                accountState = accountState,
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
                        idSubcategory = subcategoryState.value!!.id,
                        idAccount = accountState.value!!.id,
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        ExpendaApp.database.apply {
                            recordDao().insert(record)
                        }
                    }.invokeOnCompletion {
                        CoroutineScope(Dispatchers.Main).launch {
                            finish()
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.record_edit__add_button), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
