package com.kiologyn.expenda.presentation.ui.savings.modify

import androidx.activity.result.ActivityResultRegistry
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.data.db.entity.Account
import com.kiologyn.expenda.presentation.ui.savings.modify.component.AccountPicker
import com.kiologyn.expenda.presentation.ui.savings.modify.component.AmountInput
import com.kiologyn.expenda.presentation.ui.savings.modify.component.BankInput
import com.kiologyn.expenda.presentation.ui.savings.modify.component.DatePicker
import com.kiologyn.expenda.presentation.ui.savings.modify.component.categorypicker.CategoryPicker
import java.time.LocalDate


@Composable
fun SavingModify(
    modifier: Modifier = Modifier,
    accountState: MutableState<Account?>,
    bankState: MutableState<String>,
    depositOpeningDateState: MutableState<LocalDate?>,
    depositClosingDateState: MutableState<LocalDate?>,
    depositAmountState: MutableDoubleState,
    percentState: MutableDoubleState,
    lineHeight: Dp = 50.dp,
    elementsBackground: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
    shape: Shape = RoundedCornerShape(15.dp),
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(20.dp),
) = Column(
    modifier = modifier,
    verticalArrangement = verticalArrangement,
) {
    AccountPicker(
        modifier = Modifier
            .fillMaxWidth()
            .height(lineHeight+1.dp)
            .background(elementsBackground, shape)
            .clip(shape)
        ,
        accountState = accountState,
    )
    
    BankInput(
        modifier = Modifier
            .fillMaxWidth()
            .height(lineHeight)
            .background(elementsBackground, shape)
            .clip(shape),
        value = bankState.value,
        onValueChange = { newValue ->
            bankState.value = newValue
        },
        placeholder = stringResource(R.string.saving_edit__bank__placeholder),
    )
    
    DatePicker(
        modifier = Modifier
            .height(lineHeight)
            .background(elementsBackground, shape)
            .clip(shape),
        date = depositOpeningDateState.value,
        onPickDate = { newDate ->
            if (newDate == null)
                throw Exception("error date picking")
            else
                depositOpeningDateState.value = newDate
        },
        placeholder = stringResource(R.string.saving_edit__opening_date__placeholder),
    )
    
    DatePicker(
        modifier = Modifier
            .height(lineHeight)
            .background(elementsBackground, shape)
            .clip(shape),
        date = depositClosingDateState.value,
        onPickDate = { newDate ->
            depositClosingDateState.value = newDate
        },
        placeholder = stringResource(R.string.saving_edit__closing_date__placeholder),
    )
    
    AmountInput(
        modifier = Modifier
            .fillMaxWidth()
            .height(lineHeight),
        amountState = depositAmountState,
        placeholder = stringResource(R.string.saving_edit__deposit_amount__placeholder),
    )
    
    AmountInput(
        modifier = Modifier
            .fillMaxWidth()
            .height(lineHeight),
        amountState = percentState,
        placeholder = stringResource(R.string.saving_edit__percent__placeholder),
    )
}
