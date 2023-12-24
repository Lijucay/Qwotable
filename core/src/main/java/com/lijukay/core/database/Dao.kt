package com.lijukay.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quote")
    fun getAllQuotes(): List<Quote>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quotes: List<Quote>)

    @Query("DELETE FROM quote")
    suspend fun deleteAll()
}

@Dao
interface WisdomDao {
    @Query("SELECT * FROM wisdom")
    fun getAllWisdom(): List<Wisdom>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wisdom: List<Wisdom>)

    @Query("DELETE FROM wisdom")
    suspend fun deleteAll()
}