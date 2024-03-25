/*
* Copyright (C) 2024 Lijucay (Luca)
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>
* */

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
    @Query("SELECT * FROM Qwotable WHERE isOwn = 0")
    fun getQwotablesFlow(): Flow<List<Qwotable>>

    @Query("SELECT * FROM Qwotable")
    fun getQwotables(): List<Qwotable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(qwotables: List<Qwotable>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(qwotable: Qwotable)

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

    @Query("SELECT * FROM Qwotable WHERE language = :lang")
    fun getFilteredQwotable(lang: String): Flow<List<Qwotable>>
}