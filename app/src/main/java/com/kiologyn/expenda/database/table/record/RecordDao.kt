package com.kiologyn.expenda.database.table.record

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface RecordDao {
    @Insert
    suspend fun insert(record: Record)
    @Query("""
        SELECT record.id
        FROM record
        ORDER BY datetime DESC
    """)
    suspend fun getAllIdsDESC(): List<Int>
    @Query("""
        SELECT
            record.datetime AS datetime,
            record.amount AS amount,
            record.description AS description,
            subcategory.name AS subcategoryName,
            record.id = (
                SELECT subqueryrecord.id
                FROM record AS subqueryrecord
                WHERE DATE(subqueryrecord.datetime, 'unixepoch') = DATE(record.datetime, 'unixepoch')
                ORDER BY subqueryrecord.datetime DESC
                LIMIT 1
            ) AS isDailyFirst
        FROM record
        JOIN subcategory ON record.idSubcategory = subcategory.id
        WHERE record.id = :id
    """)
    suspend fun getByIdWithSubcategoryAndDailyFirstFlag(id: Int): RecordWithSubcategoryNameWithDailyFirstFlag
    @Query("""
        SELECT COALESCE(
            (
                SELECT record.id
                FROM record
                WHERE DATE(record.datetime, 'unixepoch') = (
                    SELECT DATE(sqrecord.datetime, 'unixepoch')
                    FROM record AS sqrecord
                    WHERE DATE(sqrecord.datetime, 'unixepoch') >= DATE(:date, 'unixepoch')
                    ORDER BY sqrecord.datetime
                    LIMIT 1
                )
                ORDER BY record.datetime DESC
                LIMIT 1
            ),
            (
                SELECT record.id
                FROM record
                ORDER BY record.datetime DESC
                LIMIT 1
            )
        )
    """)
    suspend fun getIdOfDailyLast(date: Long): Int?
    @Query("""
        SELECT
            record.datetime AS datetime,
            record.amount AS amount,
            record.description AS description,
            subcategory.name AS subcategoryName
        FROM record
        JOIN subcategory ON record.idSubcategory = subcategory.id
        ORDER BY record.datetime DESC
        LIMIT :quantity OFFSET :offset
    """)
    suspend fun getAllWithSubcategoryNamesWithOffsetDESC(offset: Int = 0, quantity: Int): List<RecordWithSubcategoryName>
    @Query("""
        SELECT COALESCE(SUM(record.amount), 0)
        FROM record
    """)
    suspend fun getBalance(): Double
    @Query("""
        WITH RECURSIVE dateseries AS (
            SELECT DATE(:fromDate, 'unixepoch') AS date
            UNION ALL
            SELECT DATE(date, '+1 day')
            FROM dateseries
            WHERE date < DATE(:toDate, 'unixepoch')
        ), balanceBefore AS (
            SELECT COALESCE(SUM(record.amount), 0) AS value
            FROM record
            WHERE DATE(record.datetime, 'unixepoch') < DATE(:fromDate, 'unixepoch')
        )
        SELECT
            strftime('%s', dateseries.date) AS date,
            balanceBefore.value + coalesce((
                SELECT sum(amount)
                FROM record
                WHERE DATE(record.datetime, 'unixepoch') BETWEEN DATE(:fromDate, 'unixepoch') AND dateseries.date
            ), 0) AS balance
        FROM dateseries
        LEFT JOIN balanceBefore
    """)
    suspend fun dailyBalanceRecordPerPeriod(fromDate: Long, toDate: Long): List<DailyBalanceRecord>
    @Query("""
        SELECT
            category.name AS name,
            -SUM(record.amount) AS amount
        FROM record
        JOIN subcategory ON record.idSubcategory = subcategory.id
        JOIN category ON subcategory.idCategory = category.id
        WHERE DATE(record.datetime, 'unixepoch') BETWEEN DATE(:fromDate, 'unixepoch') AND DATE(:toDate, 'unixepoch')
        AND record.amount < 0
        GROUP BY category.id
    """)
    suspend fun categoriesExpenses(fromDate: Long, toDate: Long): List<CategoryExpense>
    @Query("""
        SELECT
            subcategory.name AS name,
            -SUM(record.amount) AS amount
        FROM record
        JOIN subcategory ON record.idSubcategory = subcategory.id
        JOIN category ON subcategory.idCategory = category.id
        WHERE DATE(record.datetime, 'unixepoch') BETWEEN DATE(:fromDate, 'unixepoch') AND DATE(:toDate, 'unixepoch')
        AND record.amount < 0
        AND category.name = :categoryName
        GROUP BY subcategory.id
    """)
    suspend fun subcategoriesExpensesByCategory(fromDate: Long, toDate: Long, categoryName: String): List<CategoryExpense>
}

data class DailyBalanceRecord(
    val date: Long,
    val balance: Double,
)

data class CategoryExpense(
    val name: String,
    val amount: Double,
)

data class RecordWithSubcategoryName(
    val datetime: Long,
    val amount: Double,
    val description: String,
    val subcategoryName: String,
)

data class RecordWithSubcategoryNameWithDailyFirstFlag(
    val datetime: Long,
    val amount: Double,
    val description: String,
    val subcategoryName: String,
    val isDailyFirst: Boolean,
)
