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

package com.lijukay.quotesAltDesign.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lijukay.quotesAltDesign.data.local.model.DBQwotable
import com.lijukay.quotesAltDesign.data.local.model.OwnQwotable
import kotlinx.coroutines.flow.Flow

@Dao
interface QwotableDao {

    /**
     * Gets an asynchronous data stream of Qwotables
     * @return A [Flow] of [List] of [DBQwotable]
     * */
    @Query("SELECT * FROM DBQwotable")
    fun getQwotables(): Flow<List<DBQwotable>>

    /**
     * Gets an asynchronous data stream of Qwotables where the field [DBQwotable.isFavorite] is true
     * @return A [Flow] of [List] of [DBQwotable]
     * */
    @Query("SELECT * FROM DBQwotable WHERE isFavorite = 1")
    fun getFavoriteQwotables(): Flow<List<DBQwotable>>

    /**
     * Gets an asynchronous data stream of Qwotables that are user defined
     * @return A [Flow] of [List] of [OwnQwotable]
     * */
    @Query("SELECT * FROM OwnQwotable")
    fun getOwnQwotables(): Flow<List<OwnQwotable>>

    /**
     * Gets the database entries of [DBQwotable] as a list
     * @return A [List] of [DBQwotable]
     * */
    @Query("SELECT * FROM DBQwotable")
    suspend fun getQwotableAsList(): List<DBQwotable>

    /**
     * Gets the database entries of [OwnQwotable] as a list
     * @return A [List] of [OwnQwotable]
     * */
    @Query("SELECT * FROM OwnQwotable")
    suspend fun getOwnQwotablesAsList(): List<OwnQwotable>

    /**
     * Receive a single [DBQwotable] where [DBQwotable.quote] is equal to the quote, passed as a
     * parameter
     * @param quote The quote, that the [DBQwotable] has
     * @return The [DBQwotable] or null if no database entry exists with that quote
     * */
    @Query("select * from DBQwotable where quote = :quote")
    suspend fun getSingleDBQwotable(quote: String): DBQwotable?

    /**
     * Inserts a [DBQwotable] to the database
     * @return A long, that is -1 when the operation failed
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(localQwotable: DBQwotable): Long

    /**
     * Inserts an [OwnQwotable] to the database
     * @return A long, that is -1 when the operation failed
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ownQwotable: OwnQwotable): Long

    /**
     * Inserts a [List] of [DBQwotable] into the database
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(localQwotables: List<DBQwotable>)

    /**
     * Updates a [DBQwotable] in the database
     * @return An integer, that indicates how many entries were updated
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateQwotable(localQwotable: DBQwotable): Int

    /**
     * Updates an [OwnQwotable] in the database
     * @return An integer, that indicates how many entries were updated
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE, entity = OwnQwotable::class)
    fun updateOwnQwotable(ownQwotable: OwnQwotable): Int

    /**
     * Deletes all Qwotables from the database where the field [DBQwotable.isFavorite] is true
     * */
    @Query("DELETE FROM DBQwotable WHERE isFavorite = 1")
    suspend fun deleteAllFavorites()

    /**
     * Deletes all [OwnQwotable]s from the database
     * */
    @Query("DELETE FROM OwnQwotable")
    suspend fun deleteAllOwn()

    /**
     * Deletes a [DBQwotable] from the database where [DBQwotable.id] is equal to the parameter,
     * that was passed to this function
     * @param id The id of the Qwotable
     * @return An integer, that indicates how many entries were deleted
     * */
    @Query("DELETE FROM DBQwotable WHERE id = :id")
    suspend fun deleteQwotable(id: Int): Int

    /**
     * Deletes an [OwnQwotable] from the database where [OwnQwotable.id] is equal to the parameter,
     * that was passed to this function
     * @param id The id of the Qwotable
     * @return An integer, that indicates how many entries were deleted
     * */
    @Query("DELETE FROM OwnQwotable WHERE id = :id")
    suspend fun deleteOwnQwotable(id: Int): Int

    /**
     * Retrieves the [DBQwotable]-entries of the database, where the field [DBQwotable.language]
     * matches the passed language and orders it by the id in a descending order
     * @param language The language, the Qwotables should be in
     * @return A [Flow] of [List] of [DBQwotable]
     * */
    @Query("SELECT * FROM DBQwotable WHERE language = :language ORDER BY id DESC")
    fun getLanguageFilteredQwotables(language: String): Flow<List<DBQwotable>>

    /**
     * Retrieves the [DBQwotable]-entries of the database, where the field [DBQwotable.language]
     * matches the passed language and orders it by the id in a descending order
     * @param language The language, the Qwotables should be in
     * @return A [List] of [DBQwotable]
     * */
    @Query("SELECT * FROM DBQwotable WHERE language = :language")
    fun getLanguageFilteredQwotablesAsList(language: String): List<DBQwotable>
}