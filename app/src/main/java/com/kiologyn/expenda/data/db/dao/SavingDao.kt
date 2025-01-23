package com.kiologyn.expenda.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.kiologyn.expenda.data.db.entity.Saving


@Dao
interface SavingDao {
    @Query("""
        SELECT
            saving.id AS id,
            saving.bank AS bank,
            saving.depositOpeningDate AS depositOpeningDate,
            saving.depositClosingDate AS depositClosingDate,
            saving.depositAmount AS depositAmount,
            saving.percent AS percent,
            account.id AS accountId,
            account.name AS accountName,
            account.currencyCode AS accountCurrencyCode
        FROM saving
        JOIN account ON saving.idAccount = account.id
    """)
    suspend fun getAllWithAccountName(): List<SavingWithAccount>
    @Query("""
        SELECT
            saving.id AS id,
            saving.bank AS bank,
            saving.depositOpeningDate AS depositOpeningDate,
            saving.depositClosingDate AS depositClosingDate,
            saving.depositAmount AS depositAmount,
            saving.percent AS percent,
            account.id AS accountId,
            account.name AS accountName,
            account.currencyCode AS accountCurrencyCode
        FROM saving
        JOIN account ON saving.idAccount = account.id
        WHERE saving.id = :id
    """)
    suspend fun getById(id: Int): SavingWithAccount
    @Query("""
        DELETE
        FROM saving
        WHERE saving.id = :id
    """)
    suspend fun deleteById(id: Int)
    @Upsert
    suspend fun upsert(saving: Saving)
    @Insert
    suspend fun insert(saving: Saving)
}

data class SavingWithAccount(
    val id: Int,
    val bank: String,
    val depositOpeningDate: Long,
    val depositClosingDate: Long?,
    val depositAmount: Double,
    val percent: Double,
    val accountId: Int,
    val accountName: String,
    val accountCurrencyCode: String,
)