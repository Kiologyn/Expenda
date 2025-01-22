package com.kiologyn.expenda.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "account",
    indices = [
        Index(
            value = ["name"],
            unique = true,
        ),
    ],
)
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "currencyCode") val currencyCode: String,
)
