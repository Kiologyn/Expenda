package com.kiologyn.expenda.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kiologyn.expenda.R
import com.kiologyn.expenda.data.db.entity.Account
import com.kiologyn.expenda.data.db.dao.AccountDao
import com.kiologyn.expenda.data.db.entity.Category
import com.kiologyn.expenda.data.db.dao.CategoryDao
import com.kiologyn.expenda.data.db.entity.Record
import com.kiologyn.expenda.data.db.dao.RecordDao
import com.kiologyn.expenda.data.db.dao.SavingDao
import com.kiologyn.expenda.data.db.entity.Subcategory
import com.kiologyn.expenda.data.db.dao.SubcategoryDao
import com.kiologyn.expenda.data.db.entity.Saving


@Database(
    entities = [
        Record::class,
        Subcategory::class,
        Category::class,
        Account::class,
        Saving::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class ExpendaDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun subcategoryDao(): SubcategoryDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun savingDao(): SavingDao

    companion object {
        fun build(context: Context): ExpendaDatabase {
            return Room.databaseBuilder(
                context,
                ExpendaDatabase::class.java,
                context.getString(R.string.database_name),
            ).build()
        }
    }
}
