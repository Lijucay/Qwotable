package com.lijukay.core.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.lijukay.core.database.Quote
import com.lijukay.core.database.QwotableDatabase
import com.lijukay.core.database.Wisdom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class QuotesUtil(private val context: Context) {

    companion object {
        private const val QUOTES_URL = "https://lijukay.github.io/Qwotable/quotes/quotes.json"
        private const val WISDOM_URL = "https://lijukay.github.io/Qwotable/wisdom/wisdom.json"
    }

    private val database by lazy {
        QwotableDatabase.getDatabase(context)
    }

    suspend fun getAndInsert(requestType: String, requestObject: String, callback: (List<*>) -> Unit) {
        val isConnected = ConnectionUtil(context).isConnected

        if (requestType == "get" && requestObject == "quotes") {
            getQuotes {
                if (it.isEmpty() && isConnected) {
                    CoroutineScope(Dispatchers.IO).launch {
                        parse(QUOTES_URL) { items ->
                            callback(items)
                        }
                    }
                } else {
                    callback(it)
                }
            }
        } else if (requestType == "get" && requestObject == "wisdom") {
            getWisdom {
                if (it.isEmpty() && isConnected) {
                    CoroutineScope(Dispatchers.IO).launch {
                        parse(WISDOM_URL) { items ->
                            callback(items)
                        }
                    }
                } else {
                    callback(it)
                }
            }
        }
    }

    private suspend fun parse(stringUrl: String, callback: (List<*>) -> Unit) {
        val url = URL(stringUrl)
        val urlConnection = url.openConnection() as HttpsURLConnection

        try {
            val items: MutableList<*>
            val inputStream: InputStream = BufferedInputStream(urlConnection.inputStream)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = StringBuilder()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                jsonString.append(line)
            }
            bufferedReader.close()

            val gson = Gson()

            val itemType = if (stringUrl == QUOTES_URL) {
                object : TypeToken<List<Quote>>() {}.type
            } else {
                object : TypeToken<List<Wisdom>>() {}.type
            }

            items = gson.fromJson(
                jsonString.toString(),
                itemType
            )

            if (stringUrl == QUOTES_URL) {
                insertToDatabase(items)
            } else if (stringUrl == WISDOM_URL) {
                insertToDatabase(items)
            }

            withContext(Dispatchers.Main) {
                callback(items)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        } finally {
            urlConnection.disconnect()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun insertToDatabase(items: List<*>) {
        if (items.any { it is Quote }) {
            database.quoteDao().insert(items as List<Quote>)
        } else if (items.any { it is Wisdom }) {
            database.wisdomDao().insert(items as List<Wisdom>)
        } else {
            throw IllegalArgumentException("${items.javaClass.simpleName} is not a valid type of element for this action")
        }
    }

    private suspend fun getQuotes(callback: (List<Quote>) -> Unit) {
        val quotes = withContext(Dispatchers.IO) {
            database.quoteDao().getAllQuotes()
        }
        callback(quotes)
    }

    private suspend fun getWisdom(callback: (List<Wisdom>) -> Unit) {
        val wisdom = withContext(Dispatchers.IO) {
            database.wisdomDao().getAllWisdom()
        }
        callback(wisdom)
    }
}