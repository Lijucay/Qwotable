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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lijukay.core.R
import com.lijukay.core.database.Qwotable
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.core.utils.QwotableViewModelFactory
import com.lijukay.quotesAltDesign.App
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.data.model.Screens
import com.lijukay.quotesAltDesign.ui.composables.widgets.BottomNavigationBar
import com.lijukay.quotesAltDesign.ui.composables.widgets.TopAppBar
import com.lijukay.quotesAltDesign.ui.composables.widgets.openScreen
import com.lijukay.quotesAltDesign.ui.dialogs.AddEditDialog
import com.lijukay.quotesAltDesign.ui.dialogs.ErrorDialog
import com.lijukay.quotesAltDesign.ui.dialogs.FilterBottomSheetDialog
import com.lijukay.quotesAltDesign.ui.dialogs.InformationDialog
import com.lijukay.quotesAltDesign.ui.dialogs.ModalBottomSheetDialog
import com.lijukay.quotesAltDesign.ui.navigation.screens.HomeScreen
import com.lijukay.quotesAltDesign.ui.navigation.screens.OwnQwotablesScreen
import com.lijukay.quotesAltDesign.ui.navigation.screens.QwotableScreen
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val qwotableViewModel: QwotableViewModel by viewModels {
        QwotableViewModelFactory((application as App).repository)
    }

    private val uiViewModel: UIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QwotableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainLayout()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainLayout(modifier: Modifier = Modifier) {
        val navController = rememberNavController()
        val showEditorOptions = remember { mutableStateOf(false) }
        val showFilterIcon = remember { mutableStateOf(false) }
        val showQwotableOptionsBottomSheet = remember { mutableStateOf(false) }
        val currentQwotable = remember { mutableStateOf<Qwotable?>(null) }
        val showAddEditDialog = remember { mutableStateOf(false) }
        val showDeletionWarningDialog = remember { mutableStateOf(false) }
        val showFilterDialog = remember { mutableStateOf(false) }
        val showInformationDialog = remember { mutableStateOf(false) }
        val showUpdateDialog = remember { mutableStateOf(false) }

        DisposableEffect(key1 = uiViewModel) {
            val showBottomSheetObserver = Observer<Boolean> { showDialog ->
                showQwotableOptionsBottomSheet.value = showDialog
            }
            val currentQwotableObserver = Observer<Qwotable?> { qwotable ->
                currentQwotable.value = qwotable
            }
            val showFilterIconObserver = Observer<Boolean> { showIcon ->
                showFilterIcon.value = showIcon
            }
            val showAddFABObserver = Observer<Boolean> { showButton ->
                showEditorOptions.value = showButton
            }
            val showEditingDialog = Observer<Boolean> { showDialog ->
                showAddEditDialog.value = showDialog
            }
            val showWarningDialogObserver = Observer<Boolean> { showDialog ->
                showDeletionWarningDialog.value = showDialog
            }
            val showFilterDialogObserver = Observer<Boolean> { showDialog ->
                showFilterDialog.value = showDialog
            }
            val showInformationDialogObserver = Observer<Boolean> { showDialog ->
                showInformationDialog.value = showDialog
            }
            val showUpdateInformationDialog = Observer<Boolean> { showDialog ->
                showUpdateDialog.value = showDialog
            }

            uiViewModel.showQwotableOptionsBottomSheet.observeForever(showBottomSheetObserver)
            uiViewModel.currentSelectedQwotable.observeForever(currentQwotableObserver)
            uiViewModel.showFilterIcon.observeForever(showFilterIconObserver)
            uiViewModel.showAddFloatingActionButton.observeForever(showAddFABObserver)
            uiViewModel.showAddEditQwotableDialog.observeForever(showEditingDialog)
            uiViewModel.showErrorWarningDialog.observeForever(showWarningDialogObserver)
            uiViewModel.showFilterBottomSheet.observeForever(showFilterDialogObserver)
            uiViewModel.showInformationDialog.observeForever(showInformationDialogObserver)
            qwotableViewModel.qwotableFileUpdateAvailable.observeForever(showUpdateInformationDialog)

            onDispose {
                uiViewModel.showQwotableOptionsBottomSheet.removeObserver(showBottomSheetObserver)
                uiViewModel.currentSelectedQwotable.removeObserver(currentQwotableObserver)
                uiViewModel.showFilterIcon.removeObserver(showFilterIconObserver)
                uiViewModel.showAddFloatingActionButton.removeObserver(showAddFABObserver)
                uiViewModel.showAddEditQwotableDialog.removeObserver(showEditingDialog)
                uiViewModel.showErrorWarningDialog.removeObserver(showWarningDialogObserver)
                uiViewModel.showFilterBottomSheet.removeObserver(showFilterDialogObserver)
                uiViewModel.showInformationDialog.removeObserver(showInformationDialogObserver)
                qwotableViewModel.qwotableFileUpdateAvailable.removeObserver(showUpdateInformationDialog)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = stringResource(id = R.string.qwotable),
                    showSettingsIcon = true,
                    showBackIcon = false,
                    showFilterIcon = showFilterIcon.value,
                    uiViewModel = uiViewModel,
                    callback = null
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) },
            modifier = modifier.fillMaxSize(),
            floatingActionButton = {
                AnimatedVisibility(visible = showEditorOptions.value) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            uiViewModel.setCurrentSelectedQwotable(null)
                            uiViewModel.setShowAddEditQwotableDialog(true)
                        }
                    ) {
                        Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
                        Text(
                            modifier = modifier.padding(start = 4.dp),
                            text = stringResource(R.string.create_qwotable)
                        )
                    }
                }
            }
        ) { contentPadding ->
            NavHost(
                navController = navController,
                startDestination = Screens.Home.route,
                modifier = modifier.padding(paddingValues = contentPadding)
            ) {
                composable(route = Screens.Qwotable.route) {
                    QwotableScreen(qwotableViewModel = qwotableViewModel, uiViewModel = uiViewModel)
                }
                composable(route = Screens.Home.route) {
                    HomeScreen(uiViewModel = uiViewModel, qwotableViewModel = qwotableViewModel)
                }
                composable(route = Screens.OwnQwotables.route) {
                    OwnQwotablesScreen(
                        qwotableViewModel = qwotableViewModel,
                        uiViewModel = uiViewModel
                    )
                }
            }

            intent?.getStringExtra("action")?.let { action ->
                if (action == "download") {
                    openScreen(Screens.Qwotable.route, navController)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            destination.route?.let { uiViewModel.setCurrentPageRoute(it) }
        }

        if (showAddEditDialog.value) {
            AddEditDialog(
                qwotableViewModel = qwotableViewModel,
                uiViewModel = uiViewModel
            ) {
                uiViewModel.setShowAddEditQwotableDialog(false)
            }
        }

        if (showQwotableOptionsBottomSheet.value) {
            currentQwotable.value?.let { current ->
                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()

                ModalBottomSheetDialog(
                    onDismissRequest = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                uiViewModel.setShowQwotableOptionsBottomSheet(false)
                            }
                        }
                    },
                    sheetState = sheetState,
                    showEditorOptions = showEditorOptions.value,
                    currentQwotable = current,
                    qwotableViewModel = qwotableViewModel,
                    uiViewModel = uiViewModel,
                    onEditingRequest = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                uiViewModel.setShowQwotableOptionsBottomSheet(false)
                                uiViewModel.setShowAddEditQwotableDialog(true)
                            }
                        }
                    },
                    onDeletionRequest = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                uiViewModel.setShowQwotableOptionsBottomSheet(false)
                                uiViewModel.setShowDeletionWarningDialog(true)
                            }
                        }
                    }
                )
            }
        }

        if (showDeletionWarningDialog.value) {
            ErrorDialog(
                title = stringResource(id = R.string.delete_qwotable),
                message = stringResource(id = R.string.delete_qwotable_warning),
                onConfirmationRequest = {
                    uiViewModel.setShowDeletionWarningDialog(false)
                    qwotableViewModel.deleteSingleQwotable(currentQwotable.value!!)
                },
                onDismissRequest = { uiViewModel.setShowDeletionWarningDialog(false) }
            )
        }

        if (showFilterDialog.value) {
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()

            FilterBottomSheetDialog(
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            uiViewModel.setShowFilterBottomSheet(false)
                        }
                    }
                },
                sheetState = sheetState,
                uiViewModel = uiViewModel
            )
        }

        if (showInformationDialog.value) {
            InformationDialog(
                title = stringResource(id = R.string.about_random_quotes_title),
                message = stringResource(id = R.string.about_random_quotes_message),
                showCancel = false,
                onDismissRequest = { uiViewModel.setShowInformationDialog(false) }
            ) {
                uiViewModel.setShowInformationDialog(false)
            }
        }

        if (showUpdateDialog.value) {
            InformationDialog(
                title = stringResource(id = R.string.update_available),
                message = stringResource(id = R.string.update_message),
                showCancel = true,
                onDismissRequest = {
                    qwotableViewModel.updateFileUpdateAvailability(false)
                },
                onConfirmationRequest = {
                    CoroutineScope(Dispatchers.IO).launch {
                        qwotableViewModel.updateQwotableDatabase()
                    }
                }
            )
        }
    }
}