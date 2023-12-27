package com.kiologyn.expenda.database.table.record

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface RecordDao {
    @Insert
    suspend fun insert(record: Record)
    @Query("""
        SELECT *
        FROM record
    """)
    suspend fun getAll(): List<Record>
    @Query("""
        SELECT
            record.datetime as datetime,
            record.amount as amount,
            record.description as description,
            subcategory.name as subcategoryName
        FROM record JOIN subcategory ON record.idSubcategory = subcategory.id
        ORDER BY datetime DESC
    """)
    suspend fun getAllWithSubcategoryNamesDESC(): List<RecordWithSubcategoryName>
    @Query("""
        SELECT SUM(record.amount)
        FROM record
    """)
    suspend fun getBalance(): Double
}

data class RecordWithSubcategoryName(
    val datetime: Long,
    val amount: Double,
    val description: String,
    val subcategoryName: String,
)
