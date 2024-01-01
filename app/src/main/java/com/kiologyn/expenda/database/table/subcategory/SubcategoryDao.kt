package com.kiologyn.expenda.database.table.subcategory

import androidx.room.Dao
import androidx.room.Query


@Dao
interface SubcategoryDao {
    @Query("""
        SELECT *
        FROM subcategory
    """)
    suspend fun getAll(): List<Subcategory>
    @Query("""
        INSERT INTO subcategory (name, idCategory)
        SELECT :newSubcategoryName, category.id
        FROM category
        WHERE category.name = :categoryName
    """)
    fun create(categoryName: String, newSubcategoryName: String)
    @Query("""
        SELECT *
        FROM subcategory
        WHERE subcategory.idCategory = :id
    """)
    suspend fun getAllByCategoryId(id: Int): List<Subcategory>
    @Query("""
        SELECT *
        FROM subcategory
        WHERE subcategory.id = :id
    """)
    suspend fun getById(id: Int): Subcategory?
}
