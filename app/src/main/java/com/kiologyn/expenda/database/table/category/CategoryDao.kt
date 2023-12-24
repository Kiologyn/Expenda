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
    @Query("""
        SELECT * FROM category
    """)
    suspend fun getAll(): List<Category>
    @Query("""
        SELECT * FROM category WHERE category.id = :id
    """)
    suspend fun getById(id: Int): Category?
}
