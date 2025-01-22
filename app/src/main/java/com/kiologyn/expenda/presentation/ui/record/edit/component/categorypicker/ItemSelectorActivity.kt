package com.kiologyn.expenda.presentation.ui.record.edit.component.categorypicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.utils.Helper
import com.kiologyn.expenda.data.db.entity.Category
import com.kiologyn.expenda.data.db.entity.Subcategory
import com.kiologyn.expenda.presentation.theme.Black40
import com.kiologyn.expenda.presentation.theme.Black50
import com.kiologyn.expenda.presentation.theme.ExpendaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategorySelectorActivity : ComponentActivity() {
    companion object {
        const val SELECTED_ID_EXTRA_NAME = "selectedCategory"
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpendaTheme {
                var isEditMode by remember { mutableStateOf(false) }
                var categories by remember { mutableStateOf<List<Category>>(emptyList()) }

                var refresh by remember { mutableStateOf(true) }
                LaunchedEffect(refresh) {
                    if (refresh) {
                        ExpendaApp.database.apply {
                            categories = categoryDao().getAll()
                        }
                        refresh = false
                    }
                }

                var chosenCategory by remember { mutableStateOf<Category?>(null) }
                val modifyDialogTitle by remember(chosenCategory) { mutableStateOf(
                    if (chosenCategory == null) "Add a category"
                    else "Rename the category"
                ) }
                var textInputValue by remember(chosenCategory) { mutableStateOf(
                    chosenCategory?.name ?: ""
                ) }
                var openModifyDialog by remember { mutableStateOf(false) }
                if (openModifyDialog)
                    Dialog(
                        onDismissRequest = {
                            openModifyDialog = false
                        },
                        properties = DialogProperties(
                            usePlatformDefaultWidth = false
                        ),
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
                                    modifier = Modifier.fillMaxWidth(),
                                    text = modifyDialogTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                )

                                TextField(
                                    value = textInputValue,
                                    onValueChange = { value ->
                                        if (value.length <= Helper.CATEGORIES_MAX_LENGTH)
                                            textInputValue = value
                                    },
                                    singleLine = true,
                                    placeholder = { Text("Category name", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,

                                        unfocusedContainerColor = Black40,
                                        focusedContainerColor = Black50,
                                    )
                                )

                                Row(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .padding(top = 5.dp)
                                        .fillMaxWidth()
                                    ,
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    if (chosenCategory != null)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f)
                                            ,
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                        ExpendaApp.database.apply {
                                                            categoryDao().delete(chosenCategory!!.name)
                                                        }
                                                    }.invokeOnCompletion {
                                                        openModifyDialog = false
                                                        refresh = true
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = null,
                                                    tint = Color(0xFFBD3131),
                                                )
                                            }
                                        }

                                    TextButton(onClick = {
                                        openModifyDialog = false
                                    }) {
                                        Text("Cancel")
                                    }
                                    TextButton(onClick = {
                                        if (textInputValue.isEmpty()) return@TextButton
                                        CoroutineScope(Dispatchers.IO).launch {
                                            ExpendaApp.database.apply {
                                                val categoryName = textInputValue.trim(' ', '\n')
                                                categoryDao().run {
                                                    if (chosenCategory == null)
                                                        create(categoryName)
                                                    else
                                                        rename(
                                                            chosenCategory!!.name,
                                                            categoryName,
                                                        )
                                                }
                                            }
                                        }.invokeOnCompletion {
                                            openModifyDialog = false
                                            refresh = true
                                        }
                                    }) {
                                        Text("OK")
                                    }
                                }
                            }
                        }
                    }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                    ,
                    topBar = {
                        TopAppBar(
                            title = { Text("Choose a category") },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        finish()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            },
                            actions = {
                                Row(
                                    modifier = Modifier.padding(end = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (isEditMode) {
                                        IconButton(onClick = {
                                            chosenCategory = null
                                            openModifyDialog = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                            )
                                        }
                                        IconButton(onClick = {
                                            isEditMode = false
                                        }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = null,
                                            )
                                        }
                                    } else {
                                        IconButton(onClick = {
                                            isEditMode = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    },
                    content = { padding: PaddingValues ->
                        LazyColumn(
                            modifier = Modifier
                                .padding(padding)
                            ,
                        ) {
                            items(categories) { category: Category ->
                                ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            if (isEditMode) {
                                                chosenCategory = category
                                                openModifyDialog = true
                                            } else {
                                                val startActivityIntent = Intent(applicationContext, SubcategorySelectorActivity::class.java)
                                                startActivityIntent.putExtra(
                                                    SubcategorySelectorActivity.RECEIVED_ID_EXTRA_NAME, category.id)

                                                activityResultRegistry.register(
                                                    "subcategoryRegisterKey",
                                                    ActivityResultContracts.StartActivityForResult(),
                                                ) { result ->
                                                    if (result.resultCode == RESULT_OK) {
                                                        val subcategoryId = result.data?.getIntExtra(
                                                            SubcategorySelectorActivity.SELECTED_ID_EXTRA_NAME, -1)
                                                        if (subcategoryId !in listOf(null, -1)) {
                                                            val resultIntent = Intent()
                                                            resultIntent.putExtra(
                                                                SELECTED_ID_EXTRA_NAME, subcategoryId)
                                                            setResult(Activity.RESULT_OK, resultIntent)
                                                            finish()
                                                        }
                                                    }
                                                }.launch(startActivityIntent)
                                            }
                                        }
                                    ,
                                    headlineContent = {
                                        Text(category.name)
                                    },
                                    trailingContent = {
                                        if (isEditMode)
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                            )
                                    },
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                )
            }
        }
    }
}

class SubcategorySelectorActivity : ComponentActivity() {
    companion object {
        const val RECEIVED_ID_EXTRA_NAME = "receivedSubcategory"
        const val SELECTED_ID_EXTRA_NAME = "selectedSubcategory"
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpendaTheme {
                var isEditMode by remember { mutableStateOf(false) }
                var category by remember { mutableStateOf<Category?>(null) }
                var subcategories by remember { mutableStateOf<List<Subcategory>>(emptyList()) }

                var refresh by remember { mutableStateOf(true) }
                LaunchedEffect(refresh) {
                    if (refresh) {
                        val categoryId = intent.getIntExtra(RECEIVED_ID_EXTRA_NAME, -1)
                        if (categoryId != -1) {
                            ExpendaApp.database.apply {
                                category = categoryDao().getById(categoryId)
                                subcategories = subcategoryDao().getAllByCategoryId(categoryId)
                            }
                        }
                        refresh = false
                    }
                }

                var chosenSubcategory by remember { mutableStateOf<Subcategory?>(null) }
                val modifyDialogTitle by remember(chosenSubcategory) { mutableStateOf(
                    if (chosenSubcategory == null) "Add a subcategory"
                    else "Rename the subcategory"
                ) }
                var textInputValue by remember(chosenSubcategory) { mutableStateOf(
                    chosenSubcategory?.name ?: ""
                ) }
                var openModifyDialog by remember { mutableStateOf(false) }
                if (openModifyDialog)
                    Dialog(
                        onDismissRequest = {
                            openModifyDialog = false
                        },
                        properties = DialogProperties(
                            usePlatformDefaultWidth = false
                        ),
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            tonalElevation = 6.dp,
                            modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .height(IntrinsicSize.Min)
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = MaterialTheme.shapes.extraLarge,
                                )
                            ,
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = modifyDialogTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                )

                                TextField(
                                    value = textInputValue,
                                    onValueChange = { value ->
                                        if (value.length <= Helper.CATEGORIES_MAX_LENGTH)
                                            textInputValue = value
                                    },
                                    singleLine = true,
                                    placeholder = { Text("Subcategory name", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,

                                        unfocusedContainerColor = Black40,
                                        focusedContainerColor = Black50,
                                    )
                                )

                                Row(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .padding(top = 5.dp)
                                        .fillMaxWidth()
                                    ,
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    if (chosenSubcategory != null)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f)
                                            ,
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                        ExpendaApp.database.apply {
                                                            subcategoryDao().delete(chosenSubcategory!!.name)
                                                        }
                                                    }.invokeOnCompletion {
                                                        openModifyDialog = false
                                                        refresh = true
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = null,
                                                    tint = Color(0xFFBD3131),
                                                )
                                            }
                                        }

                                    TextButton(onClick = {
                                        openModifyDialog = false
                                    }) {
                                        Text("Cancel")
                                    }
                                    TextButton(onClick = {
                                        if (textInputValue.isEmpty()) return@TextButton
                                        CoroutineScope(Dispatchers.IO).launch {
                                            ExpendaApp.database.apply {
                                                val categoryName = textInputValue.trim(' ', '\n')
                                                subcategoryDao().run {
                                                    if (chosenSubcategory == null)
                                                        create(
                                                            category!!.name,
                                                            categoryName,
                                                        )
                                                    else
                                                        rename(
                                                            chosenSubcategory!!.name,
                                                            categoryName,
                                                        )
                                                }
                                            }
                                        }.invokeOnCompletion {
                                            openModifyDialog = false
                                            refresh = true
                                        }
                                    }) {
                                        Text("OK")
                                    }
                                }
                            }
                        }
                    }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                    ,
                    topBar = {
                        TopAppBar(
                            title = { Text("Choose a subcategory") },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        finish()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                    )
                                }
                            },
                            actions = {
                                Row(
                                    modifier = Modifier.padding(end = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (isEditMode) {
                                        IconButton(onClick = {
                                            chosenSubcategory = null
                                            openModifyDialog = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                            )
                                        }
                                        IconButton(onClick = {
                                            isEditMode = false
                                        }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = null,
                                            )
                                        }
                                    } else {
                                        IconButton(onClick = {
                                            isEditMode = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                    },
                    content = { padding: PaddingValues ->
                        LazyColumn(
                            modifier = Modifier
                                .padding(padding)
                            ,
                        ) {
                            items(subcategories) { subcategory: Subcategory ->
                                ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            if (isEditMode) {
                                                chosenSubcategory = subcategory
                                                openModifyDialog = true
                                            } else {
                                                val resultIntent = Intent()
                                                resultIntent.putExtra(SELECTED_ID_EXTRA_NAME, subcategory.id)
                                                setResult(Activity.RESULT_OK, resultIntent)
                                                finish()
                                            }
                                        }
                                    ,
                                    headlineContent = {
                                        Text(subcategory.name)
                                    },
                                    trailingContent = {
                                        if (isEditMode)
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                            )
                                    },
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                )
            }
        }
    }
}
