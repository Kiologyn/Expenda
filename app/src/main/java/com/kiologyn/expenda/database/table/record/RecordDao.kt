package com.kiologyn.expenda.database.table.record

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface RecordDao {
    @Insert
    suspend fun insert(record: Record)
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
        SELECT COALESCE(SUM(record.amount), 0)
        FROM record
    """)
    suspend fun getBalance(): Double
    @Query("""
        WITH RECURSIVE dateseries AS (
            SELECT DATE(:fromDate/1000, 'unixepoch') AS date
            UNION ALL
            SELECT DATE(date, '+1 day')
            FROM dateseries
            WHERE date < DATE(:toDate/1000, 'unixepoch')
        ), balanceBefore AS (
            SELECT COALESCE(SUM(record.amount), 0) AS value
            FROM record
            WHERE DATE(record.datetime/1000, 'unixepoch') < DATE(:fromDate/1000, 'unixepoch')
        )
        SELECT
            strftime('%s', dateseries.date)*1000 AS date,
            balanceBefore.value + coalesce((
                SELECT sum(amount)
                FROM record
                WHERE DATE(record.datetime/1000, 'unixepoch') BETWEEN DATE(:fromDate/1000, 'unixepoch') AND dateseries.date
            ), 0) AS balance
        FROM dateseries
        LEFT JOIN balanceBefore
    """)
    suspend fun dailyBalanceRecordPerPeriod(fromDate: Long, toDate: Long): List<DailyBalanceRecord>
}

data class DailyBalanceRecord(
    val date: Long,
    val balance: Double,
)

data class RecordWithSubcategoryName(
    val datetime: Long,
    val amount: Double,
    val description: String,
    val subcategoryName: String,
)
