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

import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.local.QwotableDao
import com.lijukay.quotesAltDesign.data.local.model.DBQwotable
import com.lijukay.quotesAltDesign.data.local.model.LocalQwotable
import com.lijukay.quotesAltDesign.data.local.model.OwnQwotable
import com.lijukay.quotesAltDesign.data.remote.model.responses.RemoteQwotable
import com.lijukay.quotesAltDesign.data.shared.Qwotable
import com.lijukay.quotesAltDesign.data.utils.StringValue
import com.lijukay.quotesAltDesign.domain.util.ConnectionHelper
import com.lijukay.quotesAltDesign.domain.util.DefUtils.ifNull
import com.lijukay.quotesAltDesign.domain.util.DefUtils.isNotNullOrBlank
import com.lijukay.quotesAltDesign.domain.util.QwotableFileUpdateUtil
import com.lijukay.quotesAltDesign.domain.util.RandomQuote
import com.lijukay.quotesAltDesign.domain.util.apis.QwotableAPI
import com.lijukay.quotesAltDesign.domain.util.states.QwotableResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QwotableRepositoryImpl @Inject constructor(
    private val qwotableAPI: QwotableAPI,
    private val qwotableDao: QwotableDao,
    private val connectionHelper: ConnectionHelper,
    private val qwotableFileUpdateUtil: QwotableFileUpdateUtil,
    private val randomQuote: RandomQuote
): QwotableRepository {
    override suspend fun loadLocalQwotables(): Flow<List<LocalQwotable>> {
        return qwotableDao.getQwotables()
    }
    override suspend fun loadFavoriteQwotables(): Flow<List<LocalQwotable>> =
        qwotableDao.getFavoriteQwotables()

    override suspend fun loadOwnQwotables(): Flow<List<OwnQwotable>> =
        qwotableDao.getOwnQwotables()

    override suspend fun insertQwotable(localQwotable: LocalQwotable): Boolean {
        return when (localQwotable) {
            is DBQwotable -> qwotableDao.insert(localQwotable) != -1L
            is OwnQwotable -> qwotableDao.insert(localQwotable) != -1L
            else -> false
        }
    }

    private suspend fun loadRemoteQwotables(): QwotableResult<List<DBQwotable>> {
        val response = try {
            qwotableAPI.getQwotables()
        } catch (e: Exception) {
            return QwotableResult.Failure(
                message = e.message?.let {
                    StringValue.DynamicString(it)
                } ?: StringValue.StringResource(R.string.unknown_issue)
            )
        }

        return QwotableResult.Success(convertRemoteToLocalQwotables(response))
    }

    override suspend fun updateQwotable(localQwotable: LocalQwotable): Boolean {
        return when (localQwotable) {
            is DBQwotable -> qwotableDao.updateQwotable(localQwotable) > 0
            is OwnQwotable -> qwotableDao.updateOwnQwotable(localQwotable) > 0
            else -> false
        }
    }

    override suspend fun deleteQwotable(localQwotable: LocalQwotable): Boolean {
        return when (localQwotable) {
            is DBQwotable -> qwotableDao.deleteQwotable(localQwotable.id) > 0
            is OwnQwotable -> qwotableDao.deleteOwnQwotable(localQwotable.id) > 0
            else -> false
        }
    }

    override suspend fun refreshQwotables(): QwotableResult<Boolean?> {
        val localData = qwotableDao.getQwotableAsList()
        return if (localData.isEmpty() && connectionHelper.isConnected) {
            try {
                when (val response = loadRemoteQwotables()) {
                    is QwotableResult.Failure -> {
                        return QwotableResult.Failure(response.message)
                    }
                    is QwotableResult.Success -> {
                        response.data.forEach { qwotableDao.insert(it) }
                        val versionInfo = qwotableAPI.getQwotableVersionInfo()
                        qwotableFileUpdateUtil.updateLocalVersionNumber(versionInfo[0].version)
                        return QwotableResult.Success(null)
                    }
                }
            } catch (e: Exception) {
                return QwotableResult.Failure(
                    e.message?.let {
                        StringValue.DynamicString(it)
                    } ?: StringValue.StringResource(R.string.unknown_issue)
                )
            }
        } else if (connectionHelper.isConnected) {
            QwotableResult.Success(
                qwotableFileUpdateUtil.isUpdateAvailable()
            )
        } else {
            QwotableResult.Failure(StringValue.StringResource(R.string.full_refresh_failure))
        }
    }

    override suspend fun updateQwotableDatabase(): QwotableResult<Unit> {
        return when (val response = loadRemoteQwotables()) {
            is QwotableResult.Failure -> QwotableResult.Failure(response.message)
            is QwotableResult.Success -> {
                response.data.forEach { data -> qwotableDao.insert(data) }
                val versionInfo = qwotableAPI.getQwotableVersionInfo()
                qwotableFileUpdateUtil.updateLocalVersionNumber(versionInfo[0].version)
                QwotableResult.Success(Unit)
            }
        }
    }

    override suspend fun getRandomQuote(): QwotableResult<out Qwotable> = randomQuote.getRandomQuote()
    override suspend fun checkForAPIUpdate(): Boolean {
        return qwotableFileUpdateUtil.isUpdateAvailable()
    }

    override suspend fun loadLanguageFilteredQwotables(language: String): Flow<List<LocalQwotable>> {
        return qwotableDao.getLanguageFilteredQwotables(language)
    }

    override suspend fun loadLanguageFilteredQwotablesAsList(language: String): List<LocalQwotable> {
        return qwotableDao.getLanguageFilteredQwotablesAsList(language)
    }

    private suspend fun convertRemoteToLocalQwotables(qwotables: List<RemoteQwotable>): List<DBQwotable> {
        val localQwotableVersion = qwotableFileUpdateUtil.getLocalVersionNumber()
        val converted = mutableListOf<DBQwotable>()

        qwotables.forEach { remoteQwotable ->
            val lastChanged = remoteQwotable.lastChanged.ifNull { -1 }
            val addedIn = remoteQwotable.addedIn

            if (
                lastChanged != -1 &&
                lastChanged > localQwotableVersion &&
                addedIn < localQwotableVersion
            ) {

                // If oldQwotable is not null, changes were made to the quote field as well
                // if it is null, changes were made only to other fields
                val oldQwotable = if(remoteQwotable.oldQwotable.isNotNullOrBlank())
                    qwotableDao.getSingleDBQwotable(remoteQwotable.oldQwotable)
                else qwotableDao.getSingleDBQwotable(remoteQwotable.qwotable)

                if (oldQwotable != null) {
                    qwotableDao.updateQwotable(
                        oldQwotable.copy(
                            quote = remoteQwotable.qwotable,
                            author = remoteQwotable.author.ifNull { "" },
                            source = remoteQwotable.source.ifNull { "" }
                        )
                    )
                } else {
                    // The qwotable was not existent in the database, we want to add it
                    converted.add(
                        DBQwotable(
                            quote = remoteQwotable.qwotable,
                            author = remoteQwotable.author.ifNull { "" },
                            source = remoteQwotable.source.ifNull { "" },
                            language = remoteQwotable.language
                        )
                    )
                }
            } else if (addedIn > localQwotableVersion) {
                converted.add(
                    DBQwotable(
                        quote = remoteQwotable.qwotable,
                        author = remoteQwotable.author.ifNull { "" },
                        source = remoteQwotable.source.ifNull { "" },
                        language = remoteQwotable.language
                    )
                )
            }

        }

        return converted
    }
}