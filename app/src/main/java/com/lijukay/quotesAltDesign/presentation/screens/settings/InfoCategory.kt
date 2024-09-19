package com.lijukay.quotesAltDesign.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.GppMaybe
import androidx.compose.material.icons.rounded.LocalPolice
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.presentation.composables.preferences.Preference
import com.lijukay.quotesAltDesign.presentation.composables.preferences.PreferenceCategoryTitle
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel

@Composable
fun InfoCategory(
    onOpenLicenseScreen: () -> Unit,
    uiViewModel: UIViewModel
) {
    val context = LocalContext.current

    Column {
        PreferenceCategoryTitle(title = stringResource(id = R.string.licenses_and_privacy))
        Preference(
            title = stringResource(id = R.string.licenses),
            summary = stringResource(id = R.string.licenses_summary),
            iconVector = Icons.Rounded.LocalPolice,
            onClick = onOpenLicenseScreen
        )
        Preference(
            title = stringResource(id = R.string.your_privacy),
            summary = stringResource(id = R.string.your_privacy_summary),
            iconVector = Icons.Rounded.GppMaybe
        ) {
            uiViewModel.setInfoDialogTitle(context.getString(R.string.your_privacy))
            uiViewModel.setInfoDialogMessage(context.getString(R.string.your_privacy_message))
            uiViewModel.setShowInfoDialog(true)
        }
        Preference(
            title = stringResource(id = R.string.permissions),
            summary = stringResource(id = R.string.permissions_summary),
            iconVector = Icons.Rounded.GppGood
        ) {
            uiViewModel.setInfoDialogTitle(context.getString(R.string.permissions))
            uiViewModel.setInfoDialogMessage(context.getString(R.string.permissions_messages))
            uiViewModel.setShowInfoDialog(true)
        }
    }
}