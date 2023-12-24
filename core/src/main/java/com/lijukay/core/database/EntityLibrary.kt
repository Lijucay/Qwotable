package com.lijukay.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Quote(
    @PrimaryKey val quote: String,
    val author: String,
    val source: String,
    val language: String
)

@Entity
data class Wisdom(
    @PrimaryKey val title: String,
    val wisdom: String,
    val author: String,
    val source: String
)