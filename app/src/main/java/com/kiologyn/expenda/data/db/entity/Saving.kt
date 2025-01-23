package com.kiologyn.expenda.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "saving",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["idAccount"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ]
)
data class Saving(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "idAccount") val idAccount: Int,
    @ColumnInfo(name = "bank") val bank: String,
    @ColumnInfo(name = "depositOpeningDate") val depositOpeningDate: Long,
    @ColumnInfo(name = "depositClosingDate") val depositClosingDate: Long? = null,
    @ColumnInfo(name = "depositAmount") val depositAmount: Double,
    @ColumnInfo(name = "percent") val percent: Double,
)
