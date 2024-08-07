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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lijukay.core.R
import com.lijukay.core.models.ProgrammingQuotes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection

class RandomQuote(private val context: Context) {
    private val urls: List<Triple<URL, String, String>> = listOf(
        Triple(URL("https://api.kanye.rest/"), "quote", "Kanye API"),
        Triple(
            URL("https://api.gameofthronesquotes.xyz/v1/random"),
            "sentence",
            "Game of Thrones API"
        ),
        Triple(URL("https://stoic.tekloon.net/stoic-quote"), "quote", "Tekloon Stoic Quotes API"),
        Triple(URL("https://www.jcquotes.com/api/quotes/random"), "text", "James Clear Quotes API"),
//        Triple(URL("https://api.quotable.io/random"), "content", "Quotable API"),
//        Triple(URL("https://stoic-quotes.com/api/quote"), "text", "stoic-quotes.com")
    )

    fun getRandomQuote(quote: (String) -> Unit) {
        if (ConnectionUtil(context = context).isConnected) {
            val source = (0..1).random()

            when (source) {
                0 -> { getRandomAPIQuote { result -> quote(result) } }
                1 -> { getRandomProgrammingQuote { result -> quote(result) } }
            }
        }
        else getRandomProgrammingQuote { result -> quote(result) }
    }

    private fun getRandomAPIQuote(result: (String) -> Unit) {
        val randomNum = urls.indices.random()
        val (apiUrl, jsonObjectKey, apiName) = urls[randomNum]

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlConnection = apiUrl.openConnection() as HttpsURLConnection
                urlConnection.requestMethod = "GET"

                if (urlConnection.responseCode == HttpsURLConnection.HTTP_OK) {
                    val inputStream = BufferedInputStream(urlConnection.inputStream)
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val jsonString = bufferedReader.readText()

                    val jsonObject = JSONObject(jsonString)
                    val quote: String? = jsonObject.getString(jsonObjectKey)

                    bufferedReader.close()

                    if (quote != null) result("$quote\n\n~$apiName")
                    else result(context.getString(R.string.error_server))
                }
            } catch (e: UnknownHostException) {
                result(context.getString(R.string.error_connecting, apiUrl))
            } catch (e: JSONException) {
                val message = e.message ?: context.getString(R.string.no_message)

                result(context.getString(R.string.json_exception, apiUrl, message))
            }
        }
    }

    private fun getRandomProgrammingQuote(result: (String) -> Unit) {
        try {
            val raw = context.resources.openRawResource(R.raw.programming_quotes)
            val reader = BufferedReader(InputStreamReader(raw))
            val jsonString = reader.readText()

            val type = object : TypeToken<List<ProgrammingQuotes>>() {}.type
            val objects: List<ProgrammingQuotes> = Gson().fromJson(jsonString, type)
            val randomNum = objects.indices.random()

            val randomObject = objects[randomNum]
            val randomQuote = "${randomObject.quote}\n\n${randomObject.author}\n~Programming quotes"
            result(randomQuote)
        } catch (e: Exception) {
            result(e.message.toString())
        }
    }
}