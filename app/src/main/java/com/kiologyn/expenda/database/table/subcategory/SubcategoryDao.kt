package com.kiologyn.expenda.database.table.subcategory

import androidx.room.Dao
import androidx.room.Query


@Dao
interface SubcategoryDao {
    @Query("""
        INSERT INTO subcategory (name, idCategory)
        SELECT :newSubcategoryName, category.id
        FROM category
        WHERE category.name = :categoryName
    """)
    fun create(categoryName: String, newSubcategoryName: String)
}
