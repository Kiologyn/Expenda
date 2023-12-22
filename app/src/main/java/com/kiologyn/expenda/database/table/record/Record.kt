package com.kiologyn.expenda.database.table.record

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kiologyn.expenda.database.table.subcategory.Subcategory


@Entity(
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
    val datetime: Long,
    val amount: Double,
    val description: String = "",
    val idSubcategory: Int? = null,
)
