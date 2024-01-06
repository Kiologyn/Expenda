package com.kiologyn.expenda.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kiologyn.expenda.database.table.category.Category
import com.kiologyn.expenda.database.table.category.CategoryDao
import com.kiologyn.expenda.database.table.record.Record
import com.kiologyn.expenda.database.table.record.RecordDao
import com.kiologyn.expenda.database.table.subcategory.Subcategory
import com.kiologyn.expenda.database.table.subcategory.SubcategoryDao


@Database(
    entities = [
        Record::class,
        Subcategory::class,
        Category::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class ExpendaDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun subcategoryDao(): SubcategoryDao
    abstract fun categoryDao(): CategoryDao
    companion object {
        const val DATABASE_NAME = "database"
        fun build(context: Context): ExpendaDatabase {
            return Room.databaseBuilder(
                context,
                ExpendaDatabase::class.java,
                DATABASE_NAME,
            ).build()
        }
    }
}
