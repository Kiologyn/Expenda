package com.kiologyn.expenda.presentation.ui.record

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kiologyn.expenda.R
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.data.db.entity.Account
import com.kiologyn.expenda.data.db.entity.Record
import com.kiologyn.expenda.data.db.dao.RecordWithSubcategory
import com.kiologyn.expenda.data.db.entity.Subcategory
import com.kiologyn.expenda.utils.toLocalDateTime
import com.kiologyn.expenda.utils.toSeconds
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaTopBarActivity
import com.kiologyn.expenda.presentation.ui.record.edit.RecordModify
import kotlinx.coroutines.launch
import kotlin.math.abs


class EditRecordActivity : ExpendaTopBarActivity() {
    companion object {
        const val RECORD_ID_EXTRA_NAME = "recordId"
    }

    override val title: String
        get() = getString(R.string.record_edit__edit_title)
    override val actions: @Composable() (RowScope.() -> Unit) = {}

    private var recordId: Int = -1
    private var recordState: MutableState<RecordWithSubcategory?> = mutableStateOf(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        recordId = intent.getIntExtra(RECORD_ID_EXTRA_NAME, -1)

        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun Content() {
        LaunchedEffect(Unit) {
            ExpendaApp.database.apply {
                recordState.value = recordDao().getByIdWithSubcategory(recordId)
            }
            if (recordState.value == null) finish()
        }

        if (recordState.value == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.record_edit__loading),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            val dateTimeState = remember { mutableStateOf(recordState.value!!.datetime.toLocalDateTime()) }
            val isIncomeState = remember { mutableStateOf(recordState.value!!.amount >= 0) }
            val amountState = remember { mutableStateOf<Double?>(abs(recordState.value!!.amount)) }
            val subcategoryState = remember {
                mutableStateOf<Subcategory?>(
                    Subcategory(
                        id = recordState.value!!.subcategoryId,
                        name = recordState.value!!.subcategoryName,
                        idCategory = recordState.value!!.subcategoryCategoryId,
                    )
                )
            }
            val accountState = remember {
                mutableStateOf<Account?>(
                    Account(
                        id = recordState.value!!.accountId,
                        name = recordState.value!!.accountName,
                        currencyCode = recordState.value!!.accountCurrencyCode,
                    )
                )
            }
            val descriptionState = remember { mutableStateOf(recordState.value!!.description) }

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

                val coroutineScope = rememberCoroutineScope()
                var openDeleteDialog by remember { mutableStateOf(false) }
                if (openDeleteDialog) {
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
                                    text = stringResource(R.string.record_edit__delete_confirmation__title),
                                    style = MaterialTheme.typography.labelMedium,
                                )

                                Text(
                                    text = stringResource(R.string.record_edit__delete_confirmation__text),
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
                                        Text(stringResource(R.string.dialog__cancel))
                                    }
                                    TextButton(onClick = {
                                        coroutineScope.launch {
                                            ExpendaApp.database.apply {
                                                recordDao().deleteById(recordState.value!!.id)
                                            }
                                        }.invokeOnCompletion {
                                            finish()
                                        }
                                    }) {
                                        Text(stringResource(R.string.dialog__ok))
                                    }
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
                                amountState.value in listOf(null, 0.0) ||
                                subcategoryState.value == null
                            ) return@Button // TODO: highlight inputs with red

                            val record = Record(
                                id = recordState.value!!.id,
                                datetime = dateTimeState.value.toSeconds(),
                                amount = (if (isIncomeState.value) 1 else -1) * amountState.value!!,
                                description = descriptionState.value.trim(' ', '\n'),
                                idSubcategory = subcategoryState.value!!.id,
                                idAccount = accountState.value!!.id,
                            )
                            coroutineScope.launch {
                                ExpendaApp.database.apply {
                                    recordDao().upsert(record)
                                }
                            }.invokeOnCompletion {
                                finish()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.record_edit__save_button), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
