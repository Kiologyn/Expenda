package com.kiologyn.expenda.database.table.subcategory

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kiologyn.expenda.database.table.category.Category


@Entity(
    tableName = "subcategory",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["idCategory"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class Subcategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val idCategory: Int,
)
