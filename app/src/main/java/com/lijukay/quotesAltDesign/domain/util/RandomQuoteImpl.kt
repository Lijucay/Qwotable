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

package com.lijukay.quotesAltDesign.domain.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.local.QwotableDao
import com.lijukay.quotesAltDesign.data.local.model.ProgrammingQuote
import com.lijukay.quotesAltDesign.data.remote.model.RemoteQwotable
import com.lijukay.quotesAltDesign.data.remote.model.responses.GameOfThronesAPIResponse.Companion.toQwotable
import com.lijukay.quotesAltDesign.data.remote.model.responses.JCQuoteAPIResponse.Companion.toQwotable
import com.lijukay.quotesAltDesign.data.remote.model.responses.KanyeRestAPIResponse.Companion.toQwotable
import com.lijukay.quotesAltDesign.data.remote.model.responses.StoicQuotesAPIResponse.Companion.toQwotable
import com.lijukay.quotesAltDesign.data.remote.model.responses.TekloonStoicQuotesAPIResponse.Companion.toQwotable
import com.lijukay.quotesAltDesign.data.shared.Qwotable
import com.lijukay.quotesAltDesign.data.utils.StringValue
import com.lijukay.quotesAltDesign.domain.util.apis.GameOfThronesAPI
import com.lijukay.quotesAltDesign.domain.util.apis.JCQuotesAPI
import com.lijukay.quotesAltDesign.domain.util.apis.KanyeRestAPI
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey.INCLUDE_LOCAL_QWOTABLES
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey.INCLUDE_OWN_QWOTABLES
import com.lijukay.quotesAltDesign.domain.util.apis.StoicQuotesAPI
import com.lijukay.quotesAltDesign.domain.util.apis.TekloonStoicQuotesAPI
import com.lijukay.quotesAltDesign.domain.util.states.QwotableResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class RandomQuoteImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val connectionHelper: ConnectionHelper,
    private val qwotableDao: QwotableDao,
    private val kanyeRestAPI: KanyeRestAPI,
    private val gameOfThronesAPI: GameOfThronesAPI,
    private val tekloonStoicQuotesAPI: TekloonStoicQuotesAPI,
    private val jcQuotesAPI: JCQuotesAPI,
    private val stoicQuotesAPI: StoicQuotesAPI
) : RandomQuote {
    override suspend fun getRandomQuote(): QwotableResult<out Qwotable> {
        return if (connectionHelper.isConnected) {
            val source = (0..1).random()

            Log.e("Random quote","called with $source")

            return when (source) {
                0 -> getRandomAPIQuote()
                1 -> getLocalQuote()
                else -> QwotableResult.Failure(
                    StringValue.StringResource(R.string.random_quote_failure)
                )
            }
        } else getLocalQuote()
    }

    private suspend fun getRandomAPIQuote(): QwotableResult<RemoteQwotable> {
        val randomNum = (0..4).random()

        return try {
            when (randomNum) {
                0 -> return QwotableResult.Success(kanyeRestAPI.getRandomQuote().toQwotable())
                1 -> return QwotableResult.Success(gameOfThronesAPI.getRandomQuote().toQwotable())
                2 -> return QwotableResult.Success(tekloonStoicQuotesAPI.getRandomQuote().toQwotable())
                3 -> return QwotableResult.Success(jcQuotesAPI.getRandomQuote().toQwotable())
                4 -> return QwotableResult.Success(stoicQuotesAPI.getRandomQuote().toQwotable())
                else -> QwotableResult.Failure(
                    StringValue.StringResource(R.string.random_num_ex)
                )
            }
        } catch (e: Exception) {
            QwotableResult.Failure(
                e.message?.let { StringValue.DynamicString(it) }
                    ?: StringValue.StringResource(R.string.unknown_issue)
            )
        }
    }

    private suspend fun getLocalQuote(): QwotableResult<Qwotable> {
        val includeLocal = context.dataStore.data
            .map { preferences -> preferences[INCLUDE_LOCAL_QWOTABLES] ?: false }
            .first()

        val includeOwn = context.dataStore.data
            .map { preferences -> preferences[INCLUDE_OWN_QWOTABLES] ?: false }
            .first()

        val source = (0..2).random()

        Log.e("Random Quote", "called with $source")
        Log.e("includeL", includeLocal.toString())
        Log.e("includeO", includeOwn.toString())

        return if (includeLocal && source == 1) {
            getLocalQwotable()
        } else if (includeOwn && source == 2 && qwotableDao.getOwnQwotablesAsList().isNotEmpty()) {
            getOwnQwotable()
        } else {
            getRandomProgrammingQuote()
        }
    }

    private fun getRandomProgrammingQuote(): QwotableResult<Qwotable> {
        return try {
            val raw = context.resources.openRawResource(R.raw.programming_quotes)
            val reader = BufferedReader(InputStreamReader(raw))
            val jsonString = reader.readText()

            val type = object : TypeToken<List<ProgrammingQuote>>() {}.type
            val objects = Gson().fromJson<List<ProgrammingQuote>>(jsonString, type)
            val randomNum = objects.indices.random()

            val randomObject = objects[randomNum]
            QwotableResult.Success(
                ProgrammingQuote(
                    randomObject.quote,
                    randomObject.author
                )
            )
        } catch (e: Exception) {
            QwotableResult.Failure(
                e.message?.let {
                    StringValue.DynamicString(it)
                } ?: StringValue.StringResource(
                    R.string.unknown_issue
                )
            )
        }
    }

    private suspend fun getOwnQwotable(): QwotableResult<Qwotable> {
        Log.e("RandomQuoteImpl", "Attempted to get own qwotable")

        return try {
            val ownQwotables = qwotableDao.getOwnQwotablesAsList()

            val qwotable = ownQwotables.random()
            QwotableResult.Success(qwotable)
        } catch (e: Exception) {
            QwotableResult.Failure(
                e.message?.let {
                    StringValue.DynamicString(it)
                } ?: StringValue.StringResource(
                    R.string.unknown_issue
                )
            )
        }
    }

    private suspend fun getLocalQwotable(): QwotableResult<Qwotable> {
        Log.e("RandomQuoteImpl", "Tried to get random local")

        return try {
            val localQwotables = qwotableDao.getQwotableAsList()
            val qwotable = localQwotables.random()

            QwotableResult.Success(qwotable)
        } catch (e: Exception) {
            QwotableResult.Failure(
                e.message?.let {
                    StringValue.DynamicString(it)
                } ?: StringValue.StringResource(
                    R.string.unknown_issue
                )
            )
        }
    }
}