package com.kiologyn.expenda.database.table.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kiologyn.expenda.database.table.subcategory.Subcategory


@Entity(
    tableName = "record",
    foreignKeys = [
        ForeignKey(
            entity = Subcategory::class,
            parentColumns = ["id"],
            childColumns = ["idSubcategory"],
            onDelete = ForeignKey.NO_ACTION,
        )
    ]
)
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "datetime") val datetime: Long,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "idSubcategory", index = true) val idSubcategory: Int? = null,
)
