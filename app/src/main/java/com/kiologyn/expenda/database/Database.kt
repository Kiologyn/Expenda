package com.kiologyn.expenda.database

import androidx.room.Database
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
)
abstract class ExpendaDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun subcategoryDao(): SubcategoryDao
    abstract fun categoryDao(): CategoryDao
}
