package com.kiologyn.expenda

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.kiologyn.expenda.database.ExpendaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ExpendaApp : Application() {
    val DEFAULT_CATEGORIES: Map<String, List<String>> = mapOf(
        "Food" to listOf(
            "Groceries",
            "Cafe/Bar",
            "Restaurant",
        ),
        "Shopping" to listOf(
            "Clothes",
            "Electronics",
            "Pets",
        ),
        "Housing" to listOf(
            "Utilities",
            "Rent",
        ),
        "Transport" to listOf(
            "Taxi",
            "Bus",
            "Train",
        ),
        "Investments" to listOf(
            "Savings",
            "Finance",
        ),
        "Income" to listOf(
            "Wage",
            "Gift",
            "Gambling",
        ),
    )

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = applicationContext.getSharedPreferences(Helper.SHARED_PREFERENCES_SETTINGS_NAME, Context.MODE_PRIVATE)

        if (!isDefaultCategoriesAdded())
            CoroutineScope(Dispatchers.IO).launch {
                ExpendaDatabase.build(applicationContext).apply {
                    val subcategoryDao = subcategoryDao()
                    val categoryDao = categoryDao()
                    for ((categoryName, subcategories) in DEFAULT_CATEGORIES) {
                        categoryDao.create(categoryName)
                        for (subcategoryName in subcategories)
                            subcategoryDao.create(categoryName, subcategoryName)
                    }
                }.close()

                setIsDefaultCategoriesAddedTrue()
            }
    }

    private fun isDefaultCategoriesAdded(): Boolean =
        sharedPreferences.getBoolean("isDefaultCategoriesAdded", false)
    private fun setIsDefaultCategoriesAddedTrue() {
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("isDefaultCategoriesAdded", true)
        sharedPreferencesEditor.apply()
    }
}
