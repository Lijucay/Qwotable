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

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import com.lijukay.quotesAltDesign.data.local.model.OwnQwotable
import com.lijukay.quotesAltDesign.presentation.dialogs.AddDialog
import com.lijukay.quotesAltDesign.presentation.dialogs.EditDialog
import com.lijukay.quotesAltDesign.presentation.dialogs.ErrorDialog
import com.lijukay.quotesAltDesign.presentation.dialogs.InfoDialog
import com.lijukay.quotesAltDesign.presentation.dialogs.QwotableOptionsDialog
import com.lijukay.quotesAltDesign.presentation.dialogs.ThemePickerDialog
import com.lijukay.quotesAltDesign.presentation.screens.AppScreen
import com.lijukay.quotesAltDesign.presentation.viewmodels.QwotableViewModel
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel
import com.lijukay.quotesAltDesign.presentation.dialogs.FilterBottomSheetDialog
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme
import com.lijukay.quotesAltDesign.domain.util.ClipboardUtil.copyToClipboard
import com.lijukay.quotesAltDesign.domain.util.ConnectionHelperImpl
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey
import com.lijukay.quotesAltDesign.domain.util.dataStore
import com.lijukay.quotesAltDesign.domain.util.states.Languages
import com.lijukay.quotesAltDesign.domain.util.states.ThemeMode
import com.lijukay.quotesAltDesign.presentation.dialogs.SharePreferenceDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val uiViewModel: UIViewModel by viewModels()
    private val qwotableViewModel: QwotableViewModel by viewModel()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            val showFilterDialog by uiViewModel.showFilterDialog.collectAsState()

            val showQwotableOptionsDialog by uiViewModel.showQwotableOptionsDialog.collectAsState()
            val currentQwotable by uiViewModel.currentSelectedLocalQwotable.collectAsState()

            val showAddDialog by uiViewModel.showAddDialog.collectAsState()
            val showEditDialog by uiViewModel.showEditDialog.collectAsState()

            val showErrorDialog by uiViewModel.showErrorDialog.collectAsState()
            val errorDialogMessage by uiViewModel.errorDialogMessage.collectAsState()

            val updateAvailable by qwotableViewModel.updateAvailable.collectAsState()

            val showInfoDialog by uiViewModel.showInfoDialog.collectAsState()
            val infoDialogTitle by uiViewModel.infoDialogTitle.collectAsState()
            val infoDialogMessage by uiViewModel.infoDialogMessage.collectAsState()
            val infoDialogAction by uiViewModel.infoDialogConfirmAction.collectAsState()
            val infoDialogDismissAction by uiViewModel.infoDialogDismissAction.collectAsState()

            val language by uiViewModel.selectedLanguage.collectAsState()

            val isQwotableError by qwotableViewModel.error.collectAsState()
            val qwotableErrorMessage by qwotableViewModel.errorMessage.collectAsState()

            val showThemePicker by uiViewModel.showThemePicker.collectAsState()
            val showSharePreferenceListener by uiViewModel.showSharePreferenceDialog.collectAsState()

            if (isQwotableError) {
                qwotableErrorMessage?.asString(context).let {
                    uiViewModel.setErrorDialogMessage(it)
                    uiViewModel.setShowErrorDialog(true)
                }
            }

            val loadRandomQuote by context.dataStore.data
                .map { preferences -> preferences[PreferenceKey.SHOW_RANDOM_QUOTES] ?: true }
                .collectAsState(initial = true)

            val themeMode by context.dataStore.data
                .map { preferences -> preferences[PreferenceKey.DARK_MODE_KEY]
                    ?: ThemeMode.SYSTEM_MODE.ordinal }
                .collectAsState(initial = ThemeMode.SYSTEM_MODE.ordinal)

            LaunchedEffect(Unit) {
                uiViewModel.setShowFavorites(
                    context.dataStore.data.map { preferences ->
                        preferences[PreferenceKey.SHOW_FAVORITES_KEY] ?: false
                    }.first()
                )

                with(qwotableViewModel) {
                    if (ConnectionHelperImpl(this@MainActivity).isConnected) {
                        refreshQwotables()
                    }
                    loadFavoriteQwotables()
                    loadOwnQwotables()
                }
            }

            LaunchedEffect(loadRandomQuote) {
                if (loadRandomQuote) qwotableViewModel.getRandomQuote()
            }

            LaunchedEffect(language) {
                if (language == Languages.DEFAULT) {
                    qwotableViewModel.loadLanguageFilteredQwotables(
                        language.displayName.asString(context)
                    )
                } else {
                    qwotableViewModel.loadLanguageFilteredQwotables(language.filterName)
                }
            }

            QwotableTheme(
                darkTheme = when (themeMode) {
                    ThemeMode.LIGHT_MODE.ordinal -> false
                    ThemeMode.DARK_MODE.ordinal -> true
                    else -> isSystemInDarkTheme()
                }
            ) {
                AppScreen(qwotableViewModel = qwotableViewModel, uiViewModel = uiViewModel)

                if (showFilterDialog) {
                    val sheetState = rememberModalBottomSheetState()

                    FilterBottomSheetDialog(
                        onDismissRequest = {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) uiViewModel.setShowFilterDialog(false)
                            }
                        },
                        sheetState = sheetState,
                        uiViewModel = uiViewModel
                    )
                }

                if (showThemePicker) {
                    val sheetState = rememberModalBottomSheetState()

                    ThemePickerDialog(
                        onDismissRequest = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    uiViewModel.setShowThemePickerDialog(false)
                                }
                            }
                        },
                        sheetState = sheetState,
                        onSaveAppTheme = {
                            scope.launch {
                                context.dataStore.edit { preferences ->
                                    preferences[PreferenceKey.DARK_MODE_KEY] = it
                                }
                            }
                        }
                    )
                }

                if (showSharePreferenceListener) {
                    val sheetState = rememberModalBottomSheetState()

                    SharePreferenceDialog(
                        sheetState = sheetState,
                        onDismissRequest = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) uiViewModel.setShowSharePreferenceDialog(false)
                            }
                        },
                        onSaveSharePreference = {
                            scope.launch {
                                context.dataStore.edit { preferences ->
                                    preferences[PreferenceKey.SHARE_PREFERENCE_KEY] = it
                                }
                            }.invokeOnCompletion {
                                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                if (showQwotableOptionsDialog) {
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                    currentQwotable?.let { qwotable ->
                        QwotableOptionsDialog(
                            onDismissRequest = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        uiViewModel.setShowQwotableOptionsDialog(false)
                                        uiViewModel.setCurrentSelectedQwotable(null)
                                    }
                                }
                            },
                            sheetState = sheetState,
                            qwotableViewModel = qwotableViewModel,
                            currentLocalQwotable = qwotable,
                            onEditingRequest = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        uiViewModel.setShowQwotableOptionsDialog(false)
                                        uiViewModel.setShowEditQwotableDialog(true)
                                    }
                                }
                            },
                            onDeletionRequest = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        uiViewModel.setShowQwotableOptionsDialog(false)
                                        uiViewModel.setInfoDialogTitle(
                                            context.getString(R.string.delete_dialog_title)
                                        )
                                        uiViewModel.setInfoDialogMessage(
                                            context.getString(R.string.delete_dialog_message)
                                        )
                                        uiViewModel.setInfoDialogAction {
                                            qwotableViewModel.deleteQwotable(qwotable)
                                        }
                                        uiViewModel.setShowInfoDialog(true)
                                    }
                                }
                            }
                        )
                    }
                }

                if (showAddDialog) {
                    val sheetState = rememberModalBottomSheetState()

                    AddDialog(
                        sheetState = sheetState,
                        onAddQwotable = { qwotable ->
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    uiViewModel.setShowAddQwotableDialog(false)
                                    qwotableViewModel.insertQwotable(qwotable)
                                }
                            }
                        },
                        onDismissRequest = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    uiViewModel.setShowAddQwotableDialog(false)
                                }
                            }
                        }
                    )
                }

                if (showEditDialog) {
                    val sheetState = rememberModalBottomSheetState()

                    currentQwotable?.let { cQwotable ->
                        if (cQwotable is OwnQwotable) {
                            EditDialog(
                                sheetState = sheetState,
                                localQwotable = cQwotable,
                                onEditQwotable = { qwotable ->
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            uiViewModel.setShowEditQwotableDialog(false)
                                            qwotableViewModel.updateQwotable(qwotable)
                                        }
                                    }
                                },
                                onDismissRequest = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            uiViewModel.setShowEditQwotableDialog(false)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                if (showErrorDialog) {
                    val sheetState = rememberModalBottomSheetState()

                    errorDialogMessage?.let { message ->
                        ErrorDialog(
                            onDismissRequest = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        uiViewModel.setShowErrorDialog(false)
                                        qwotableViewModel.resetError()
                                    }
                                }
                            },
                            onCopyRequest = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        uiViewModel.setShowErrorDialog(false)
                                        message.copyToClipboard(context)
                                    }
                                }
                            },
                            errorMessage = message,
                            sheetState = sheetState
                        )
                    }
                }

                if (updateAvailable) {
                    uiViewModel.setInfoDialogTitle(context.getString(R.string.update_available_title))
                    uiViewModel.setInfoDialogMessage(context.getString(R.string.update_available))
                    uiViewModel.setInfoDialogAction { qwotableViewModel.updateQwotableDatabase() }
                    uiViewModel.setInfoDialogDismissAction {
                        qwotableViewModel.hideUpdateAvailable()
                        uiViewModel.resetInfoDialog()
                        uiViewModel.resetInfoDialogDismissAction()
                    }
                    uiViewModel.setShowInfoDialog(true)
                }

                if (showInfoDialog) {
                    val sheetState = rememberModalBottomSheetState()

                    infoDialogTitle?.let { title ->
                        infoDialogMessage?.let { message ->
                            InfoDialog(
                                title = title,
                                message = message,
                                sheetState = sheetState,
                                confirmationIcon = Icons.Rounded.Check,
                                onConfirmPressed = infoDialogAction?.let {
                                    {
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                it()
                                                uiViewModel.resetInfoDialog()
                                                uiViewModel.resetInfoDialogAction()
                                            }
                                        }
                                    }
                                },
                                onDismissRequest = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            infoDialogDismissAction()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}