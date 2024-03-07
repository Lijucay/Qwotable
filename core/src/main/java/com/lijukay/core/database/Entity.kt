package com.lijukay.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["qwotable"], unique = true)])
data class Qwotable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val qwotable: String,
    val author: String,
    val source: String,
    val language: String,
    val isFavorite: Boolean = false,
    val isOwn: Boolean = false
)