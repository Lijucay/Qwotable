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

package com.lijukay.quotesAltDesign.ui.navigation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.GppMaybe
import androidx.compose.material.icons.rounded.LocalPolice
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Observer
import com.lijukay.core.R
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.core.utils.QwotableViewModelFactory
import com.lijukay.quotesAltDesign.App
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.ui.composables.preferences.Preference
import com.lijukay.quotesAltDesign.ui.composables.preferences.PreferenceCategoryTitle
import com.lijukay.quotesAltDesign.ui.composables.widgets.TopAppBar
import com.lijukay.quotesAltDesign.ui.dialogs.InformationDialog
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    private val uiViewModel: UIViewModel by viewModels()
    private val qwotableViewModel: QwotableViewModel by viewModels {
        QwotableViewModelFactory((application as App).repository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QwotableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsPreferenceList()
                }
            }
        }
    }

    @Composable
    fun SettingsPreferenceList(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val informationDialogTitle = remember { mutableStateOf("") }
        val informationDialogMessage = remember { mutableStateOf("") }
        val showInformationDialog = remember { mutableStateOf(false) }
        val showCancel = remember { mutableStateOf(false) }
        val action: MutableState<() -> Unit> = remember { mutableStateOf(
            {
                showInformationDialog.value = false
            }
        ) }

        DisposableEffect(key1 = uiViewModel) {
            val observer = Observer<Boolean> { show ->
                showInformationDialog.value = show
            }

            uiViewModel.showInformationDialog.observeForever(observer)

            onDispose {
                uiViewModel.showInformationDialog.removeObserver(observer)
            }
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = stringResource(id = R.string.settings),
                    showSettingsIcon = false,
                    showFilterIcon = false,
                    uiViewModel = uiViewModel
                ) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        ) { paddingValues ->
            Column(modifier = modifier.padding(paddingValues)) {
                PreferenceCategoryTitle(title = stringResource(id = R.string.app_settings))
                Preference(
                    title = stringResource(id = R.string.check_for_updates),
                    summary = stringResource(id = R.string.check_for_updates_summary),
                    iconVector = Icons.Rounded.Update
                ) {
                    qwotableViewModel.checkForUpdates { updateAvailable ->
                        if (updateAvailable) {
                            informationDialogTitle.value = getString(R.string.update_available)
                            informationDialogMessage.value = getString(R.string.update_message)
                            showCancel.value = true
                            action.value = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    qwotableViewModel.updateQwotableDatabase()
                                }.invokeOnCompletion {
                                    showInformationDialog.value = false
                                }
                            }
                        } else {
                            informationDialogTitle.value = getString(R.string.no_update_available)
                            informationDialogMessage.value = getString(R.string.no_update_message)
                            showCancel.value = false
                            action.value = {
                                showInformationDialog.value = false
                            }
                        }

                        showInformationDialog.value = true
                    }
                }
                /*Preference(
                    title = stringResource(id = R.string.language),
                    summary = stringResource(id = R.string.current_language),
                    iconVector = Icons.Rounded.Language
                ) {
                    /*Show language chooser dialog*/
                }*/

                PreferenceCategoryTitle(title = stringResource(id = R.string.licenses_and_privacy))
                Preference(
                    title = stringResource(id = R.string.licenses),
                    summary = stringResource(id = R.string.licenses_summary),
                    iconVector = Icons.Rounded.LocalPolice
                ) {
                    startActivity(Intent(context, LicensesActivity::class.java))
                }
                Preference(
                    title = stringResource(id = R.string.your_privacy),
                    summary = stringResource(id = R.string.your_privacy_summary),
                    iconVector = Icons.Rounded.GppMaybe
                ) {
                    informationDialogTitle.value = context.getString(R.string.your_privacy)
                    informationDialogMessage.value =
                        context.getString(R.string.your_privacy_message)
                    uiViewModel.setShowInformationDialog(true)
                }
                Preference(
                    title = stringResource(id = R.string.permissions),
                    summary = stringResource(id = R.string.permissions_summary),
                    iconVector = Icons.Rounded.GppGood
                ) {
                    informationDialogTitle.value = context.getString(R.string.permissions)
                    informationDialogMessage.value =
                        context.getString(R.string.permissions_messages)
                    uiViewModel.setShowInformationDialog(true)
                }
            }
        }

        if (showInformationDialog.value) {
            InformationDialog(
                title = informationDialogTitle.value,
                message = informationDialogMessage.value,
                showCancel = showCancel.value,
                onDismissRequest = { uiViewModel.setShowInformationDialog(false) },
                onConfirmationRequest = action.value
            )
        }
    }
}