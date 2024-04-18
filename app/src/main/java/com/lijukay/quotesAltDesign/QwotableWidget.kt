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

package com.lijukay.quotesAltDesign

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.lijukay.core.R
import com.lijukay.core.database.Qwotable
import com.lijukay.quotesAltDesign.ui.navigation.activities.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

object QwotableWidget : GlanceAppWidget() {
    private val quoteKey = stringPreferencesKey(name = "quote")
    @StringRes val noQuotesStringId = R.string.no_quotes_available_widget_method

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val quote = currentState(key = quoteKey) ?: context.getString(noQuotesStringId)
                if (quote == context.getString(noQuotesStringId)) onFirstCreation(context, id)

                Column(
                    modifier = GlanceModifier
                        .padding(all = 16.dp)
                        .background(colorProvider = GlanceTheme.colors.primaryContainer)
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(GlanceTheme.colors.onPrimaryContainer)
                            .cornerRadius(12.dp)
                    ) {
                        LazyColumn(
                            modifier = GlanceModifier,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(1) {
                                Column {
                                    Text(
                                        modifier = GlanceModifier.padding(16.dp),
                                        text = quote,
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                            color = GlanceTheme.colors.primaryContainer
                                        )
                                    )

                                    Row(
                                        modifier = GlanceModifier.padding(16.dp)
                                    ) {
                                        Button(
                                            text = context.getString(R.string.renew),
                                            onClick = actionRunCallback<QuoteActionCallback>()
                                        )

                                        if (quote == context.getString(noQuotesStringId))
                                            Button(
                                                text = context.getString(R.string.open_app),
                                                onClick = actionRunCallback<OpenAppActionCallback>()
                                            )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onFirstCreation(context: Context, glanceId: GlanceId) {
        updateQwotableWidget(context, glanceId)
    }

    fun updateQwotableWidget(context: Context, glanceId: GlanceId) {
        Log.e(javaClass.simpleName, "called uQW")

        CoroutineScope(context = Dispatchers.IO).launch {
            val repository = (context.applicationContext as App).repository
            val lang = Locale.getDefault().language

            val quotes: List<Qwotable> = when (lang) {
                "de" -> repository.getFilteredQwotables("German")
                "fr" -> repository.getFilteredQwotables("French")
                else -> repository.getFilteredQwotables("English")
            }

            var randQuote = context.getString(R.string.no_quotes_available_widget_method)
            if (quotes.isNotEmpty()) {
                quotes[quotes.indices.random()].qwotable.apply { randQuote = this }
            }

            withContext(context = Dispatchers.Main) {
                updateAppWidgetState(context = context, glanceId = glanceId) { pref ->
                    pref[quoteKey] = randQuote
                }
                QwotableWidget.update(context = context, id = glanceId)
            }
        }
    }

    fun updateOnReboot(context: Context?) {
        context?.let {
            CoroutineScope(Dispatchers.IO).launch {
                updateAll(it)
            }
        }
    }
}

class QuoteActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        QwotableWidget.updateQwotableWidget(context, glanceId)
    }
}

class OpenAppActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("action", "download")
        context.startActivity(intent)
    }
}

class SimpleQwotableWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = QwotableWidget
}