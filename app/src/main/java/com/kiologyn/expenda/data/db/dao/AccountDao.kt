package com.kiologyn.expenda.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kiologyn.expenda.data.db.entity.Account


@Dao
interface AccountDao {
    @Insert
    suspend fun insert(record: Account)
    @Update
    suspend fun update(record: Account)
    @Delete
    suspend fun delete(record: Account)
    @Query("""
        SELECT * FROM account
    """)
    suspend fun getAll(): List<Account>
    @Query("""
        SELECT
            account.name AS name,
            COALESCE(SUM(record.amount), 0) AS balance
        FROM account
        LEFT JOIN record ON record.idAccount = account.id
            AND DATE(record.datetime, 'unixepoch') BETWEEN DATE(:fromDate, 'unixepoch') AND DATE(:toDate, 'unixepoch')
        GROUP BY account.name
    """)
    suspend fun getAllWithBalancesPerPeriod(fromDate: Long, toDate: Long): List<AccountWithBalance>
    @Query("""
        SELECT
            account.currencyCode AS code,
            COALESCE(SUM(record.amount), 0) AS balance
        FROM account
        LEFT JOIN record ON record.idAccount = account.id
            AND DATE(record.datetime, 'unixepoch') BETWEEN DATE(:fromDate, 'unixepoch') AND DATE(:toDate, 'unixepoch')
        GROUP BY account.currencyCode
    """)
    suspend fun getAllCurrenciesWithBalancesPerPeriod(fromDate: Long, toDate: Long): List<CurrencyWithBalance>
}

data class AccountWithBalance(
    val name: String,
    val balance: Double,
)

data class CurrencyWithBalance(
    val code: String,
    val balance: Double,
)
