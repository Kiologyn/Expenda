package com.kiologyn.expenda.database.table.category

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
)
