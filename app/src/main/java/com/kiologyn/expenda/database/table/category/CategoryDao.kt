package com.kiologyn.expenda.database.table.category

import androidx.room.Dao
import androidx.room.Query


@Dao
interface CategoryDao {
    @Query("""
        INSERT INTO category (name)
        SELECT :name
    """)
    fun create(name: String)
}
