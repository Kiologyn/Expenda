package com.kiologyn.expenda.app.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import com.kiologyn.expenda.R
import com.kiologyn.expenda.db.ExpendaDatabase
import com.kiologyn.expenda.data.db.entity.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.prefs.Preferences


class ExpendaApp : Application() {
    private lateinit var SHARED_PREFERENCES__FIRST_LAUNCH_STRING: String
    private val DEFAULT_CATEGORIES: Map<String, List<String>> = mapOf(
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
    private val DEFAULT_ACCOUNTS: List<Account> = listOf(
        Account(name = "Cash", currencyCode = "BYN"),
        Account(name = "Card Visa", currencyCode = "BYN"),
        Account(name = "Card Mastercard", currencyCode = "USD"),
    )


    @OptIn(DelicateCoroutinesApi::class)
    private val coroutineScope: CoroutineScope = GlobalScope

    private lateinit var generalSharedPreferences: SharedPreferences

    companion object {
        lateinit var database: ExpendaDatabase
        lateinit var settingsPreferences: DataStore<Preferences>
    }

    override fun onCreate() {
        super.onCreate()

        SHARED_PREFERENCES__FIRST_LAUNCH_STRING = getString(R.string.shared_preferences__first_launch)

        generalSharedPreferences = applicationContext.getSharedPreferences(
            getString(R.string.shared_preferences__general),
            Context.MODE_PRIVATE,
        )

        database = ExpendaDatabase.build(applicationContext)

        // run code for the app first launch
        if (!generalSharedPreferences.getBoolean(SHARED_PREFERENCES__FIRST_LAUNCH_STRING, false)) {
            coroutineScope.launch {
                database.apply {
                    // save default categories
                    val subcategoryDao = subcategoryDao()
                    val categoryDao = categoryDao()
                    for ((categoryName, subcategories) in DEFAULT_CATEGORIES) {
                        categoryDao.create(categoryName)
                        for (subcategoryName in subcategories)
                            subcategoryDao.create(categoryName, subcategoryName)
                    }

                    // save default accounts
                    DEFAULT_ACCOUNTS.forEach { account ->
                        accountDao().insert(account)
                    }
                }
            }.invokeOnCompletion { exception ->
                if (exception == null) {
                    // set as first launch is completed
                    val sharedPreferencesEditor = generalSharedPreferences.edit()
                    sharedPreferencesEditor.putBoolean(SHARED_PREFERENCES__FIRST_LAUNCH_STRING, true)
                    sharedPreferencesEditor.apply()
                }
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        coroutineScope.cancel()
    }
}
