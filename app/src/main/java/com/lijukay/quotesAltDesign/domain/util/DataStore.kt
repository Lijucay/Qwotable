package com.lijukay.quotesAltDesign.domain.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object PreferenceKey {
    val SHARE_PREFERENCE_KEY = intPreferencesKey("share_preference")
    val SHOW_FAVORITES_KEY = booleanPreferencesKey("show_favorites")
    val SHOW_RANDOM_QUOTES = booleanPreferencesKey("show_random_quotes")
    val INCLUDE_LOCAL_QWOTABLES = booleanPreferencesKey("include_local_qwotables")
    val INCLUDE_OWN_QWOTABLES = booleanPreferencesKey("include_own_qwotables")
    val DARK_MODE_KEY = intPreferencesKey("dark_mode_key")
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")