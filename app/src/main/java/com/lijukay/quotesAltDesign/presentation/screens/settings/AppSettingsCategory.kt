package com.lijukay.quotesAltDesign.presentation.screens.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Update
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.domain.util.ConnectionHelperImpl
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey.DARK_MODE_KEY
import com.lijukay.quotesAltDesign.domain.util.dataStore
import com.lijukay.quotesAltDesign.domain.util.states.ThemeMode
import com.lijukay.quotesAltDesign.presentation.composables.preferences.Preference
import com.lijukay.quotesAltDesign.presentation.composables.preferences.PreferenceCategoryTitle
import com.lijukay.quotesAltDesign.presentation.viewmodels.QwotableViewModel
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel
import kotlinx.coroutines.flow.map

@Composable
fun AppSettingsCategory(
    qwotableViewModel: QwotableViewModel,
    uiViewModel: UIViewModel
) {
    val context = LocalContext.current

    val updateAvailable by qwotableViewModel.updateAvailable.collectAsState()
    val appTheme by context.dataStore.data
        .map { preferences -> preferences[DARK_MODE_KEY] ?: ThemeMode.SYSTEM_MODE.ordinal }
        .collectAsState(ThemeMode.SYSTEM_MODE.ordinal)

    Column {
        PreferenceCategoryTitle(
            title = stringResource(R.string.app_settings)
        )

        Preference(
            title = stringResource(R.string.check_for_updates),
            summary = stringResource(R.string.check_for_updates_summary),
            iconVector = Icons.Rounded.Update
        ) {
            if (ConnectionHelperImpl(context).isConnected) {
                qwotableViewModel.checkForUpdates().invokeOnCompletion {
                    if (!updateAvailable) {
                        uiViewModel.setInfoDialogTitle(context.getString(R.string.no_update_available_title))
                        uiViewModel.setInfoDialogMessage(context.getString(R.string.no_update_available))
                        uiViewModel.setShowInfoDialog(true)
                    }
                }
            }
        }

        Preference(
            title = stringResource(R.string.app_theme),
            summary = stringResource(
                when (appTheme) {
                    ThemeMode.LIGHT_MODE.ordinal -> R.string.light_mode
                    ThemeMode.DARK_MODE.ordinal -> R.string.dark_mode
                    else -> R.string.system_mode
                }
            ),
            iconVector = when (appTheme) {
                ThemeMode.LIGHT_MODE.ordinal -> Icons.Rounded.LightMode
                ThemeMode.DARK_MODE.ordinal -> Icons.Rounded.DarkMode
                else -> {
                    if (isSystemInDarkTheme())
                        Icons.Rounded.DarkMode
                    else Icons.Rounded.LightMode
                }
            }
        ) {
            uiViewModel.setShowThemePickerDialog(true)
        }
    }
}