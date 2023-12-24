package com.kiologyn.expenda.ui.page.add.component.categorypicker

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.kiologyn.expenda.Helper
import com.kiologyn.expenda.LocalActivityResultRegistry
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.subcategory.Subcategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CategoryPicker(
    modifier: Modifier = Modifier,
    subcategoryState: MutableState<Subcategory?> = remember { mutableStateOf(null as Subcategory?) }
) {
    val categoryTextState = remember { mutableStateOf(subcategoryState.value?.name) }

    val localContext = LocalContext.current
    val activityResultRegistry = LocalActivityResultRegistry.current
    val startForResult = remember(activityResultRegistry) {
        activityResultRegistry?.register(
            "categoryRegisterKey",
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val subcategoryId = data?.getIntExtra(CategorySelectorActivity.SELECTED_ID_EXTRA_NAME, -1)
                if (subcategoryId !in listOf(null, -1)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = Room.databaseBuilder(
                            localContext,
                            ExpendaDatabase::class.java,
                            Helper.DATABASE_NAME,
                        ).build()

                        subcategoryState.value = db.subcategoryDao().getById(subcategoryId!!)
                        categoryTextState.value =
                            if (subcategoryState.value == null) null
                            else subcategoryState.value!!.name
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                startForResult?.launch(Intent(localContext, CategorySelectorActivity::class.java))
            }
        ,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            categoryTextState.value ?: "Choose a category",
            color =
                if (categoryTextState.value == null) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface
        )

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
