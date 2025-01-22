package com.kiologyn.expenda.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kiologyn.expenda.data.db.entity.Category


@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)
    @Update
    suspend fun update(category: Category)
    @Query("""
        SELECT * FROM category WHERE category.isIncome = :isIncome
    """)
    suspend fun getAll(isIncome: Boolean): List<Category>
    @Query("""
        SELECT * FROM category WHERE category.id = :id
    """)
    suspend fun getById(id: Int): Category?
    @Query("""
        DELETE
        FROM category
        WHERE name = :name
    """)
    suspend fun delete(name: String)
}
