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

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lijukay.quotesAltDesign.data.utils.Screens
import com.lijukay.quotesAltDesign.presentation.screens.settings.SettingsScreen
import com.lijukay.quotesAltDesign.presentation.viewmodels.QwotableViewModel
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel

@Composable
fun AppScreen(
    qwotableViewModel: QwotableViewModel,
    uiViewModel: UIViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.MainScreen.route
    ) {
        composable(Screens.MainScreen.route) {
            MainScreen(qwotableViewModel, uiViewModel) {
                navController.navigate(Screens.SettingsScreen.route)
            }
        }

        composable(Screens.SettingsScreen.route) {
            SettingsScreen(
                uiViewModel = uiViewModel,
                qwotableViewModel = qwotableViewModel,
                onNavigateUp = { navController.popBackStack() },
                onOpenLicenseScreen = { navController.navigate(Screens.LicensesScreen.route) }
            )
        }

        composable(Screens.LicensesScreen.route) {
            LicensesScreen { navController.popBackStack() }
        }
    }
}