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
import android.graphics.drawable.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.absolutePadding
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object QwotableWidget : GlanceAppWidget() {
    val quoteKey = stringPreferencesKey(name = "quote")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val quote = currentState(key = quoteKey).orEmpty()
                if (quote.isEmpty()) { onFirstCreation(context = context, glanceId = id) }
                val quotesArray = mutableListOf<String>()
                quotesArray.add(quote)
                quotesArray.add(context.getString(R.string.refresh))

                Column(
                    modifier = GlanceModifier
                        .padding(all = 16.dp)
                        .background(colorProvider = GlanceTheme.colors.primaryContainer)
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = GlanceModifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .background(GlanceTheme.colors.surface)
                            .cornerRadius(12.dp)
                    ) {
                        Text(
                            text = context.getString(R.string.app_name),
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = GlanceModifier.fillMaxWidth()
                        )
                    }

                    LazyColumn(
                        modifier = GlanceModifier
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(quotesArray) { item ->
                            if (item != context.getString(R.string.refresh)) {
                                Text(
                                    modifier = GlanceModifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                        .background(GlanceTheme.colors.surface)
                                        .cornerRadius(12.dp),
                                    text = item,
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                                    )
                                )
                            } else {
                                Button(
                                    text = context.getString(R.string.refresh),
                                    onClick = actionRunCallback(callbackClass = QuoteActionCallback::class.java),
                                    modifier = GlanceModifier
                                )
                            }
                        }
                    }

                    /*Text(
                        text = quote,
                        modifier = GlanceModifier.padding(bottom = 16.dp),
                        style = TextStyle(
                            color = GlanceTheme.colors.onBackground,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            textAlign = TextAlign.Center
                        )
                    )
                    Button(
                        text = context.getString(R.string.refresh),
                        onClick = actionRunCallback(callbackClass = QuoteActionCallback::class.java),
                        modifier = GlanceModifier
                    )*/
                }
            }
        }
    }

    private fun onFirstCreation(context: Context, glanceId: GlanceId) {
        CoroutineScope(context = Dispatchers.IO).launch {
            val repository = (context.applicationContext as App).repository
            val quotes = repository.getQwotables()
            val randQuote = quotes[quotes.indices.random()].qwotable

            withContext(Dispatchers.Main) {
                updateAppWidgetState(context = context, glanceId = glanceId) { pref ->
                    pref[quoteKey] = randQuote
                }
                QwotableWidget.update(context = context, id = glanceId)
            }
        }
    }
}

class SimpleQwotableWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = QwotableWidget
}

class QuoteActionCallback: ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        CoroutineScope(context = Dispatchers.IO).launch {
            val repository = (context.applicationContext as App).repository
            val quotes = repository.getQwotables()
            val randQuote = quotes[quotes.indices.random()].qwotable

            withContext(context = Dispatchers.Main) {
                updateAppWidgetState(context = context, glanceId = glanceId) { pref ->
                    pref[QwotableWidget.quoteKey] = randQuote
                }
                QwotableWidget.update(context = context, id = glanceId)
            }
        }
    }
}