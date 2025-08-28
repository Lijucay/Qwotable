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
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.lijukay.quotesAltDesign.data.local.model.LocalQwotable
import com.lijukay.quotesAltDesign.data.repository.QwotableRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

object QwotableWidget : GlanceAppWidget() {
    private val quoteKey = stringPreferencesKey(name = "quote")
    @StringRes val noQuotesStringId = R.string.no_quotes_available_widget_method

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetEntryPoint {
        fun repository(): QwotableRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val quote = currentState(key = quoteKey) ?: context.getString(noQuotesStringId)
                if (quote == context.getString(noQuotesStringId)) onFirstCreation(context, id)

                Scaffold(
                    modifier = GlanceModifier
                        .cornerRadius(radius = 24.dp)
                        .padding(bottom = 12.dp),
                    titleBar = {
                        TitleBar(
                            modifier = GlanceModifier.padding(end = 16.dp),
                            startIcon = ImageProvider(R.drawable.ic_launcher_foreground),
                            title = context.getString(R.string.app_name),
                            actions = {
                                CircleIconButton(
                                    modifier = GlanceModifier.size(32.dp),
                                    imageProvider = ImageProvider(R.drawable.rounded_refresh_24),
                                    contentDescription = context.getString(R.string.renew),
                                    onClick = { updateQwotableWidget(context, id) }
                                )
                            }
                        )
                    }
                ) {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .cornerRadius(12.dp)
                            .background(GlanceTheme.colors.onPrimaryContainer)
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    modifier = GlanceModifier.padding(16.dp),
                                    text = quote,
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                        color = GlanceTheme.colors.primaryContainer
                                    )
                                )
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
            val entryPoint = EntryPointAccessors.fromApplication<WidgetEntryPoint>(context)
            val repository = entryPoint.repository()
            val lang = Locale.getDefault().language

            val quotes: List<LocalQwotable> = when (lang) {
                "de" -> repository.loadLanguageFilteredQwotablesAsList("German")
                "fr" -> repository.loadLanguageFilteredQwotablesAsList("French")
                else -> repository.loadLanguageFilteredQwotablesAsList("English")
            }

            var randQuote = context.getString(R.string.no_quotes_available_widget_method)
            if (quotes.isNotEmpty()) {
                quotes[quotes.indices.random()].quote.apply { randQuote = this }
            }

            withContext(context = Dispatchers.Main) {
                updateAppWidgetState(context = context, glanceId = glanceId) { pref ->
                    pref[quoteKey] = randQuote
                }
                update(context = context, id = glanceId)
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

class SimpleQwotableWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = QwotableWidget
}