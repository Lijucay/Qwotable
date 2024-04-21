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

package com.lijukay.core.utils

import android.content.Context
import android.util.Log
import com.lijukay.core.database.Qwotable
import com.lijukay.core.database.QwotableDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.jvm.Throws

class QwotableRepository(
    private val qwotableDao: QwotableDao,
    private val apiService: QwotableApiService,
    private val context: Context
) {
    private val tag = this.javaClass.simpleName

    val allQwotables = qwotableDao.getQwotablesFlow()
    val allFavorites = qwotableDao.getFavoritesQwotableFlow()
    val allOwnQwotables = qwotableDao.getOwnQwotableFlow()

    suspend fun insert(qwotable: Qwotable) {
        withContext(Dispatchers.IO) {
            qwotableDao.insert(qwotable)
        }
    }

    suspend fun updateQwotable(qwotable: Qwotable) {
        withContext(Dispatchers.IO) {
            qwotableDao.updateQwotable(qwotable)
        }
    }

    /*suspend fun deleteAllFavorites() {
        withContext(Dispatchers.IO) {
            qwotableDao.deleteAllFavorites()
        }
    }

    suspend fun deleteAllOwn() {
        withContext(Dispatchers.IO) {
            qwotableDao.deleteAllOwn()
        }
    }*/

    suspend fun deleteSingleQwotable(qwotable: Qwotable) {
        withContext(Dispatchers.IO) {
            qwotableDao.deleteSingleQwotable(qwotable.id)
        }
    }

    fun getFilteredQwotable(language: String): Flow<List<Qwotable>> {
        return qwotableDao.getFilteredQwotableFlow(language)
    }

    suspend fun getFilteredQwotables(lang: String): List<Qwotable> {
        return withContext(Dispatchers.IO) {
            return@withContext qwotableDao.getFilteredQwotable(lang)
        }
    }

    suspend fun refreshQwotable() {
        withContext(Dispatchers.IO) {
            val localData = qwotableDao.getQwotables()
            val connectionUtil = ConnectionUtil(context)

            if (localData.isEmpty() && connectionUtil.isConnected) {
                try {
                    val remoteData = apiService.getQwotables()
                    qwotableDao.insert(remoteData)
                } catch (e: Exception) {
                    Log.e(tag, e.message.toString())
                }
            }
        }
    }

    @Throws(Exception::class)
    suspend fun checkForApiUpdates(viewModel: QwotableViewModel, onUpdateCheckCompleted: (Boolean, Int) -> Unit) {
        withContext(Dispatchers.IO) {
            val response = apiService.getVersionsFile()
            val jsonFiles = response.jsonFiles
            val qwotableJSONVersion = jsonFiles[0].qwotableJsonVersion
            val apiVersionPreference = context.getSharedPreferences("Api", 0)
            val apiVersion = apiVersionPreference.getInt("version", 0)

            onUpdateCheckCompleted(apiVersion < qwotableJSONVersion, qwotableJSONVersion)

            try {


                if (qwotableJSONVersion > apiVersion) {
                    withContext(Dispatchers.Main) {
                        viewModel.updateFileUpdateAvailability(true, qwotableJSONVersion)
                        onUpdateCheckCompleted
                    }
                } else {
                    viewModel.updateFileUpdateAvailability(false)
                }
            } catch (e: Exception) {
                Log.e(tag, e.message.toString())
            }
        }
    }

    suspend fun updateQwotableDatabase(
        newVersion: Int,
        viewModel: QwotableViewModel
    ) {
        val apiVersionPreference = context.getSharedPreferences("Api", 0)
        val remoteData = apiService.getQwotables()
        qwotableDao.insert(remoteData)
        apiVersionPreference.edit().putInt("version", newVersion).apply()

        withContext(Dispatchers.Main) {
            viewModel.updateFileUpdateAvailability(false)
        }
    }
}