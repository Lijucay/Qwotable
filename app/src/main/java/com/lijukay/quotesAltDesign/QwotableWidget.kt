package com.lijukay.quotesAltDesign

import android.content.Context
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
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
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
                val quote = currentState(key = quoteKey) ?: ""
                if (quote == "") {
                    onFirstCreation(context = context, glanceId = id)
                }
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(colorProvider = GlanceTheme.colors.background)) {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(all = 16.dp),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        Text(
                            text = quote,
                            modifier = GlanceModifier.padding(bottom = 16.dp),
                            style = TextStyle(
                                color = GlanceTheme.colors.onBackground,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                textAlign = TextAlign.Center
                            )
                        )
                        Button(
                            text = "Refresh",
                            onClick = actionRunCallback(callbackClass = QuoteActionCallback::class.java),
                            modifier = GlanceModifier
                        )
                    }
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