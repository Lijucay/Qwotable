package com.lijukay.quotesAltDesign.presentation.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey.INCLUDE_LOCAL_QWOTABLES
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey.INCLUDE_OWN_QWOTABLES
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey.SHOW_RANDOM_QUOTES
import com.lijukay.quotesAltDesign.domain.util.dataStore
import com.lijukay.quotesAltDesign.presentation.composables.preferences.SwitchPreference
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun RandomQuotesCategory(

) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val showRandomQuotes by context.dataStore.data
        .map { preferences -> preferences[SHOW_RANDOM_QUOTES] ?: true }
        .collectAsState(true)
    val includeLocalQwotables by context.dataStore.data
        .map { preferences -> preferences[INCLUDE_LOCAL_QWOTABLES] ?: false }
        .collectAsState(false)
    val includeOwnQwotables by context.dataStore.data
        .map { preferences -> preferences[INCLUDE_OWN_QWOTABLES] ?: false }
        .collectAsState(false)

    Column {
        SwitchPreference(
            title = stringResource(R.string.show_random_quotes),
            summary = stringResource(
                id = if (showRandomQuotes) R.string.show_random_quotes_true
                else R.string.show_random_quotes_false
            ),
            checked = showRandomQuotes,
            iconVector = Icons.Rounded.Shuffle
        ) {
            scope.launch {
                context.dataStore.edit { preferences ->
                    preferences[SHOW_RANDOM_QUOTES] = it
                }
            }
        }

        AnimatedVisibility(showRandomQuotes) {
            Column {
                SwitchPreference(
                    title = stringResource(R.string.include_local_qwotables),
                    summary = stringResource(
                        if (includeLocalQwotables) R.string.include_local_qwotables_true
                        else R.string.include_local_qwotables_false
                    ),
                    checked = includeLocalQwotables,
                    iconVector = Icons.Rounded.Storage
                ) {
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[INCLUDE_LOCAL_QWOTABLES] = it
                        }
                    }
                }

                SwitchPreference(
                    title = stringResource(R.string.include_own_qwotables),
                    summary = stringResource(
                        if (includeOwnQwotables) R.string.include_own_qwotables_true
                        else R.string.include_own_qwotables_false
                    ),
                    checked = includeOwnQwotables,
                    iconVector = Icons.Rounded.SupervisedUserCircle
                ) {
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[INCLUDE_OWN_QWOTABLES] = it
                        }
                    }
                }
            }
        }
    }
}