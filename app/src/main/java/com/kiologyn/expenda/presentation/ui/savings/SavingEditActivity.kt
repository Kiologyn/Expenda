package com.kiologyn.expenda.presentation.ui.savings

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.kiologyn.expenda.data.db.dao.SavingWithAccount
import com.kiologyn.expenda.data.db.entity.Account
import com.kiologyn.expenda.data.db.entity.Saving
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaTopBarActivity
import com.kiologyn.expenda.presentation.ui.savings.modify.SavingModify
import com.kiologyn.expenda.utils.toLocalDate
import com.kiologyn.expenda.utils.toSeconds
import kotlinx.coroutines.launch
import java.time.LocalDate

class SavingEditActivity : ExpendaTopBarActivity() {
    companion object {
        const val SAVING_ID_EXTRA_NAME = "savingId"
    }
    
    override val title: String
        get() = if (savingId == -1) getString(R.string.saving_edit__title__add)
            else getString(R.string.saving_edit__title__edit)
    override val actions: @Composable() (RowScope.() -> Unit) = {}
    
    private var savingId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        savingId = intent.getIntExtra(SAVING_ID_EXTRA_NAME, savingId)
        
        super.onCreate(savedInstanceState)
    }
    
    @Composable
    override fun Content() {
        var saving: SavingWithAccount? by remember { mutableStateOf(null) }
        
        LaunchedEffect(Unit) {
            ExpendaApp.database.apply {
                saving = savingDao().getById(savingId)
            }
        }
        
        if (savingId != -1 && saving == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.saving_edit__loading),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            val accountState = remember { mutableStateOf(saving?.let {
                Account(
                    id = it.accountId,
                    name = it.accountName,
                    currencyCode = it.accountCurrencyCode,
                )
            } ) }
            val bankState = remember { mutableStateOf(saving?.bank ?: "") }
            val depositOpeningDateState = remember { mutableStateOf<LocalDate?>(saving?.run { depositOpeningDate.toLocalDate() } ?: LocalDate.now()) }
            val depositClosingDateState = remember { mutableStateOf(saving?.run { depositClosingDate?.toLocalDate() }) }
            val depositAmountState = remember { mutableDoubleStateOf(saving?.depositAmount ?: 0.0) }
            val percentState = remember { mutableDoubleStateOf(saving?.percent ?: 0.0) }
            
            val LINE_HEIGHT = 50.dp
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp, 50.dp)
                ,
            ) {
                SavingModify(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                    ,
                    accountState = accountState,
                    bankState = bankState,
                    depositOpeningDateState = depositOpeningDateState,
                    depositClosingDateState = depositClosingDateState,
                    depositAmountState = depositAmountState,
                    percentState = percentState,
                    lineHeight = LINE_HEIGHT,
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
                                                savingDao().deleteById(saving!!.id)
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
                    if (savingId != -1) {
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
                                .weight(1f)
                                .height(LINE_HEIGHT)
                            ,
                            onClick = {
                                coroutineScope.launch {
                                    // TODO: withdraw
                                }.invokeOnCompletion {
                                    finish()
                                }
                            }
                        ) {
                            Text(stringResource(R.string.saving_edit__withdraw), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(LINE_HEIGHT)
                        ,
                        onClick = {
                            if (
                                bankState.value == "" ||
                                accountState.value == null ||
                                depositOpeningDateState.value == null ||
                                depositAmountState.doubleValue <= 0
                            ) return@Button // TODO: highlight inputs with red
                            
                            val newSaving = Saving(
                                id = saving?.id ?: 0,
                                idAccount = accountState.value!!.id,
                                bank = bankState.value,
                                depositOpeningDate = depositOpeningDateState.value!!.toSeconds(),
                                depositClosingDate = depositClosingDateState.value?.toSeconds(),
                                depositAmount = depositAmountState.doubleValue,
                                percent = percentState.doubleValue,
                            )
                            coroutineScope.launch {
                                ExpendaApp.database.apply {
                                    savingDao().apply {
                                        if (savingId == -1)
                                            insert(newSaving)
                                        else
                                            upsert(newSaving)
                                    }
                                }
                            }.invokeOnCompletion {
                                finish()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.saving_edit__save), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}