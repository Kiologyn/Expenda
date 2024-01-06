package com.kiologyn.expenda.ui.record.modify.component.categorypicker

import android.content.Intent
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.subcategory.Subcategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CategoryPicker(
    modifier: Modifier = Modifier,
    subcategoryState: MutableState<Subcategory?> = remember { mutableStateOf(null) },
    activityResultRegistry: ActivityResultRegistry,
) {
    val categoryText by remember(subcategoryState.value) { mutableStateOf(subcategoryState.value?.name) }

    val localContext = LocalContext.current
    val startForResult = remember(activityResultRegistry) {
        activityResultRegistry.register(
            "categoryRegisterKey",
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            val subcategoryId = result.data?.getIntExtra(
                CategorySelectorActivity.SELECTED_ID_EXTRA_NAME,
                -1,
            )
            CoroutineScope(Dispatchers.IO).launch {
                subcategoryState.value = ExpendaDatabase
                    .build(localContext)
                    .subcategoryDao()
                    .getById(
                        subcategoryId ?: subcategoryState.value?.id ?: -1
                    )
            }
        }
    }

    Box(
        modifier = modifier
            .clickable {
                startForResult?.launch(Intent(localContext, CategorySelectorActivity::class.java))
            }
        ,
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = categoryText ?: "Choose a category",
            transitionSpec = {
                val duration = 300 // ms
                fadeIn(tween(duration)) togetherWith fadeOut(tween(duration))
            },
            contentAlignment = Alignment.Center,
            label = "choose category's animation",
        ) { text ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                textAlign = TextAlign.Center,
                color =
                    if (categoryText == null) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                ,
            )
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
            ,
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = Color.Gray,
        )
    }
}
