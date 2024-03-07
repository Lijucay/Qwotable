package com.lijukay.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QwotableDao {
    @Query("SELECT * FROM Qwotable")
    fun getQwotablesFlow(): Flow<List<Qwotable>>

    @Query("SELECT * FROM Qwotable")
    fun getQwotables(): List<Qwotable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(qwotables: List<Qwotable>)

    @Query("SELECT * FROM Qwotable WHERE isFavorite = 1")
    fun getFavoritesQwotableFlow(): Flow<List<Qwotable>>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateQwotable(qwotable: Qwotable)

    @Query("SELECT * FROM Qwotable WHERE isOwn = 1")
    fun getOwnQwotableFlow(): Flow<List<Qwotable>>

    @Query("DELETE FROM Qwotable WHERE isFavorite = 1")
    suspend fun deleteAllFavorites()

    @Query("DELETE FROM Qwotable WHERE isOwn = 1")
    suspend fun deleteAllOwn()

    @Query("DELETE FROM Qwotable WHERE id = :id")
    suspend fun deleteSingleQwotable(id: Int)
}