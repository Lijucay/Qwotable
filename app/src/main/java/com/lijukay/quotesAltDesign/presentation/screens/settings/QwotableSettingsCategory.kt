package com.lijukay.quotesAltDesign.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey.SHOW_FAVORITES_KEY
import com.lijukay.quotesAltDesign.domain.util.dataStore
import com.lijukay.quotesAltDesign.presentation.composables.preferences.Preference
import com.lijukay.quotesAltDesign.presentation.composables.preferences.PreferenceCategoryTitle
import com.lijukay.quotesAltDesign.presentation.composables.preferences.SwitchPreference
import com.lijukay.quotesAltDesign.presentation.viewmodels.QwotableViewModel
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun QwotableSettingsCategory(
    qwotableViewModel: QwotableViewModel,
    uiViewModel: UIViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val showFavorites by context.dataStore.data
        .map { preferences -> preferences[SHOW_FAVORITES_KEY] ?: false }
        .collectAsState(false)

    Column {
        PreferenceCategoryTitle(
            title = stringResource(R.string.qwotable_settings)
        )

        Preference(
            title = stringResource(R.string.share_preferences),
            summary = stringResource(R.string.share_preferences_summary),
            iconVector = Icons.Rounded.IosShare
        ) {
            uiViewModel.setShowSharePreferenceDialog(true)
        }

        SwitchPreference(
            title = stringResource(R.string.show_favorites_preference_title),
            summary = stringResource(
                id = if (showFavorites)
                    R.string.show_favorites_preference_true
                else R.string.show_random_quotes_false
            ),
            checked = showFavorites,
            iconVector = Icons.Rounded.Favorite
        ) {
            scope.launch {
                context.dataStore.edit { preferences ->
                    preferences[SHOW_FAVORITES_KEY] = it
                }
            }
        }
    }
}