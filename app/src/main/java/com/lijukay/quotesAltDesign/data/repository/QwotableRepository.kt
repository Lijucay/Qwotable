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

package com.lijukay.quotesAltDesign.data.repository

import com.lijukay.quotesAltDesign.data.local.model.LocalQwotable
import com.lijukay.quotesAltDesign.data.local.model.OwnQwotable
import com.lijukay.quotesAltDesign.data.shared.Qwotable
import com.lijukay.quotesAltDesign.domain.util.states.QwotableResult
import kotlinx.coroutines.flow.Flow

interface QwotableRepository {
    suspend fun loadLocalQwotables(): Flow<List<LocalQwotable>>
    suspend fun loadFavoriteQwotables(): Flow<List<LocalQwotable>>
    suspend fun loadOwnQwotables(): Flow<List<OwnQwotable>>
    suspend fun insertQwotable(localQwotable: LocalQwotable): Boolean
    suspend fun updateQwotable(localQwotable: LocalQwotable): Boolean
    suspend fun deleteQwotable(localQwotable: LocalQwotable): Boolean
    suspend fun refreshQwotables(): QwotableResult<Boolean?>
    suspend fun updateQwotableDatabase(): QwotableResult<Unit>
    suspend fun getRandomQuote(): QwotableResult<out Qwotable>
    suspend fun checkForAPIUpdate(): Boolean
    suspend fun loadLanguageFilteredQwotables(language: String): Flow<List<LocalQwotable>>
    suspend fun loadLanguageFilteredQwotablesAsList(language: String): List<LocalQwotable>
}