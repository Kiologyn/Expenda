package com.kiologyn.expenda.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.kiologyn.expenda.data.db.entity.Category


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
    @Query("""
        UPDATE category
        SET name = :newName
        WHERE name = :oldName
    """)
    suspend fun rename(oldName: String, newName: String)
    @Query("""
        DELETE
        FROM category
        WHERE name = :name
    """)
    suspend fun delete(name: String)
}
