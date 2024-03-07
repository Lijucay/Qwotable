package com.lijukay.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.lijukay.core.database.Qwotable
import com.lijukay.core.database.QwotableDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QwotableRepository(
    private val qwotableDao: QwotableDao,
    private val apiService: QwotableApiService,
    private val context: Context
) {
    suspend fun insert(qwotables: List<Qwotable>) {
        withContext(Dispatchers.IO) {
            qwotableDao.insert(qwotables)
        }
    }

    suspend fun updateQwotable(qwotable: Qwotable) {
        withContext(Dispatchers.IO) {
            qwotableDao.updateQwotable(qwotable)
        }
    }

    suspend fun deleteAllFavorites() {
        withContext(Dispatchers.IO) {
            qwotableDao.deleteAllFavorites()
        }
    }

    suspend fun deleteAllOwn() {
        withContext(Dispatchers.IO) {
            qwotableDao.deleteAllOwn()
        }
    }

    suspend fun deleteSingleQwotable(qwotable: Qwotable) {
        withContext(Dispatchers.IO) {
            qwotableDao.deleteSingleQwotable(qwotable.id)
        }
    }

    val allQwotables = qwotableDao.getQwotablesFlow()
    val allFavorites = qwotableDao.getFavoritesQwotableFlow()
    val allOwnQwotables = qwotableDao.getOwnQwotableFlow()

    suspend fun refreshQwotable() {
        withContext(Dispatchers.IO) {
            val localData = qwotableDao.getQwotables()
            val connectionUtil = ConnectionUtil(context)

            if (localData.isEmpty() && connectionUtil.isConnected) {
                val remoteData = apiService.getQwotables()
                qwotableDao.insert(remoteData)
            } else if (localData.isNotEmpty() && connectionUtil.isConnected) {
                checkForApiUpdates()
            }
        }
    }

    suspend fun checkForApiUpdates() {
        withContext(Dispatchers.IO) {
            val response = apiService.getVersionsFile()
            val jsonFiles = response.jsonFiles
            val qwotableJSONVersion = jsonFiles[0].qwotableJsonVersion
            val apiVersionPreference = context.getSharedPreferences("Api", 0)
            val apiVersion = apiVersionPreference.getInt("version", 0)

            if (qwotableJSONVersion > apiVersion) {
                updateQwotableDatabase(apiVersionPreference, qwotableJSONVersion)
            }
        }
    }

    private suspend fun updateQwotableDatabase(
        apiVersionPreference: SharedPreferences,
        newVersion: Int
    ) {
        val remoteData = apiService.getQwotables()
        qwotableDao.insert(remoteData)
        apiVersionPreference.edit().putInt("version", newVersion).apply()
    }
}