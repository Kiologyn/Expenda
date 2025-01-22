package com.kiologyn.expenda.presentation.ui.record.edit

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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.data.db.entity.Account
import com.kiologyn.expenda.data.db.entity.Subcategory
import com.kiologyn.expenda.presentation.ui.record.edit.component.AccountPicker
import com.kiologyn.expenda.presentation.ui.record.edit.component.AmountInput
import com.kiologyn.expenda.presentation.ui.record.edit.component.ArrowButton
import com.kiologyn.expenda.presentation.ui.record.edit.component.DateTimePicker
import com.kiologyn.expenda.presentation.ui.record.edit.component.DescriptionInput
import com.kiologyn.expenda.presentation.ui.record.edit.component.categorypicker.CategoryPicker
import java.time.LocalDateTime


@Composable
fun RecordModify(
    modifier: Modifier = Modifier,
    dateTimeState: MutableState<LocalDateTime>,
    isIncomeState: MutableState<Boolean>,
    amountState: MutableState<Double?>,
    subcategoryState: MutableState<Subcategory?>,
    accountState: MutableState<Account?>,
    descriptionState: MutableState<String>,
    activityResultRegistry: ActivityResultRegistry,
    lineHeight: Dp = 50.dp,
    elementsBackground: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
    shape: Shape = RoundedCornerShape(15.dp),
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(20.dp),
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
    ) {
        DateTimePicker(
            modifier = Modifier
                .height(lineHeight)
                .background(elementsBackground, shape)
                .clip(shape)
            ,
            dateTimeState = dateTimeState,
        )

        Row(
            modifier = Modifier.height(lineHeight),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ArrowButton(
                modifier = Modifier
                    .background(elementsBackground, shape)
                    .clip(shape)
                ,
                size = lineHeight,
                isArrowUp = isIncomeState,
            )
            AmountInput(
                modifier = Modifier
                    .weight(1f)
                    .height(lineHeight),
                amountState = amountState,
            )
        }

        CategoryPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(lineHeight)
                .background(elementsBackground, shape)
                .clip(shape)
            ,
            subcategoryState,
            activityResultRegistry,
        )

        AccountPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(lineHeight)
                .background(elementsBackground, shape)
                .clip(shape)
            ,
            accountState,
        )

        DescriptionInput(
            modifier = Modifier
                .fillMaxWidth()
                .height(lineHeight * 3)
                .background(elementsBackground, shape)
            ,
            descriptionState,
        )
    }
}
