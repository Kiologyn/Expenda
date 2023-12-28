package com.kiologyn.expenda.ui.navigation.page.home.add.component.categorypicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.kiologyn.expenda.Helper
import com.kiologyn.expenda.database.ExpendaDatabase
import com.kiologyn.expenda.database.table.category.Category
import com.kiologyn.expenda.database.table.subcategory.Subcategory
import com.kiologyn.expenda.ui.theme.ExpendaTheme


class CategorySelectorActivity : ComponentActivity() {
    companion object {
        val SELECTED_ID_EXTRA_NAME = "selectedCategory"
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpendaTheme {
                var categories by remember { mutableStateOf<List<Category>>(emptyList()) }

                LaunchedEffect(true) {
                    val db = Room.databaseBuilder(
                        applicationContext,
                        ExpendaDatabase::class.java,
                        Helper.DATABASE_NAME,
                    ).build()

                    categories = db.categoryDao().getAll()
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
                                            val startActivityIntent = Intent(applicationContext, SubcategorySelectorActivity::class.java)
                                            startActivityIntent.putExtra(SubcategorySelectorActivity.RECEIVED_ID_EXTRA_NAME, category.id)

                                            activityResultRegistry.register(
                                                "subcategoryRegisterKey",
                                                ActivityResultContracts.StartActivityForResult(),
                                            ) { result ->
                                                if (result.resultCode == RESULT_OK) {
                                                    val subcategoryId = result.data?.getIntExtra(
                                                        SubcategorySelectorActivity.SELECTED_ID_EXTRA_NAME, -1)
                                                    if (subcategoryId !in listOf(null, -1)) {
                                                        val resultIntent = Intent()
                                                        resultIntent.putExtra(SELECTED_ID_EXTRA_NAME, subcategoryId)
                                                        setResult(Activity.RESULT_OK, resultIntent)
                                                        finish()
                                                    }
                                                }
                                            }.launch(startActivityIntent)
                                        }
                                    ,
                                    headlineContent = {
                                        Text(category.name)
                                    },
                                )
                                Divider()
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
        val RECEIVED_ID_EXTRA_NAME = "receivedSubcategory"
        val SELECTED_ID_EXTRA_NAME = "selectedSubcategory"
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpendaTheme {
                var subcategories by remember { mutableStateOf<List<Subcategory>>(emptyList()) }

                LaunchedEffect(true) {
                    val db = Room.databaseBuilder(
                        applicationContext,
                        ExpendaDatabase::class.java,
                        Helper.DATABASE_NAME,
                    ).build()

                    val categoryId = intent.getIntExtra(RECEIVED_ID_EXTRA_NAME, -1)
                    if (categoryId != -1)
                        subcategories = db.subcategoryDao().getAllByCategoryId(categoryId)
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
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back",
                                    )
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
                                            val resultIntent = Intent()
                                            resultIntent.putExtra(SELECTED_ID_EXTRA_NAME, subcategory.id)
                                            setResult(Activity.RESULT_OK, resultIntent)
                                            finish()
                                        }
                                    ,
                                    headlineContent = {
                                        Text(subcategory.name)
                                    },
                                )
                                Divider()
                            }
                        }
                    }
                )
            }
        }
    }
}
