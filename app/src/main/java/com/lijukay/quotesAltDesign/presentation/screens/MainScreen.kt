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

package com.lijukay.quotesAltDesign.presentation.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.utils.Screens
import com.lijukay.quotesAltDesign.domain.util.FileUtil.createBackupFile
import com.lijukay.quotesAltDesign.domain.util.FileUtil.readFromBackupFile
import com.lijukay.quotesAltDesign.presentation.composables.NavigationScaffold
import com.lijukay.quotesAltDesign.presentation.viewmodels.QwotableViewModel
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    qwotableViewModel: QwotableViewModel,
    uiViewModel: UIViewModel,
    onSettingsPressed: () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val ownQwotables by qwotableViewModel.ownQwotables.collectAsState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data

            uri?.let {
                scope.launch {
                    val qwotables = readFromBackupFile(context, it)
                    qwotables?.forEach { ownQwotable ->
                        qwotableViewModel.insertQwotable(ownQwotable)
                    }
                }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            Screens.OwnQwotableScreen.route -> {
                uiViewModel.setShowEditorOptions(true)
                uiViewModel.setShowFilterToolIcon(false)
                uiViewModel.setShowExportIcon(true)
            }
            Screens.QwotableScreen.route -> {
                uiViewModel.setShowEditorOptions(false)
                uiViewModel.setShowFilterToolIcon(true)
                uiViewModel.setShowExportIcon(false)
            }
            else -> {
                uiViewModel.setShowEditorOptions(false)
                uiViewModel.setShowFilterToolIcon(false)
                uiViewModel.setShowExportIcon(false)
            }
        }
    }

    val currentTitle by uiViewModel.currentTitle.collectAsState()
    val showEditorOptions by uiViewModel.showEditorOptions.collectAsState()
    val showFilterIcon by uiViewModel.showFilterToolIcon.collectAsState()
    val showExportIcon by uiViewModel.showExportIcon.collectAsState()

    NavigationScaffold(
        navController = navController,
        uiViewModel = uiViewModel
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = currentTitle.asString(context))
                    },
                    actions = {
                        AnimatedVisibility(visible = showFilterIcon) {
                            IconButton(onClick = {
                                uiViewModel.setShowFilterDialog(true)
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.FilterAlt,
                                    contentDescription = stringResource(id = R.string.filter)
                                )
                            }
                        }

                        AnimatedVisibility(visible = showExportIcon) {
                            Row {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                            type = "application/octet-stream"
                                            addCategory(Intent.CATEGORY_OPENABLE)
                                        }

                                        filePickerLauncher.launch(intent)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Upload,
                                        contentDescription = null
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        createBackupFile(context, ownQwotables)
                                        Toast.makeText(
                                            context,
                                            R.string.file_created,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Download,
                                        contentDescription = null
                                    )
                                }
                            }
                        }

                        IconButton(onClick = onSettingsPressed) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = stringResource(
                                    id = R.string.settings
                                )
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showEditorOptions,
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally()
                ) {
                    ExtendedFloatingActionButton(
                        icon = { Icon(imageVector = Icons.Rounded.Edit, contentDescription = null) },
                        text = {
                            Text(
                                text = stringResource(id =  R.string.create_qwotable)
                            )
                        },
                        onClick = {
                            uiViewModel.setCurrentSelectedQwotable(null)
                            uiViewModel.setShowAddQwotableDialog(true)
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = Screens.HomeScreen.route
            ) {
                composable(Screens.QwotableScreen.route) {
                    QwotableScreen(
                        qwotableViewModel = qwotableViewModel,
                        uiViewModel = uiViewModel
                    )
                }

                composable(Screens.HomeScreen.route) {
                    HomeScreen(
                        uiViewModel = uiViewModel,
                        qwotableViewModel = qwotableViewModel
                    )
                }

                composable(Screens.OwnQwotableScreen.route) {
                    OwnQwotablesScreen(
                        qwotableViewModel = qwotableViewModel,
                        uiViewModel = uiViewModel
                    )
                }
            }
        }
    }
}