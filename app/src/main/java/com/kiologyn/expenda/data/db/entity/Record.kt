package com.kiologyn.expenda.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "record",
    foreignKeys = [
        ForeignKey(
            entity = Subcategory::class,
            parentColumns = ["id"],
            childColumns = ["idSubcategory"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["idAccount"],
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "datetime") val datetime: Long,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "idSubcategory", index = true) val idSubcategory: Int? = null,
    @ColumnInfo(name = "idAccount", index = true) val idAccount: Int? = null,
)
