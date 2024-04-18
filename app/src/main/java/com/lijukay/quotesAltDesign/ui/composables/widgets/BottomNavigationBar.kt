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

package com.lijukay.quotesAltDesign.ui.composables.widgets

import android.content.Context
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.lijukay.quotesAltDesign.data.model.BottomNavigationItem
import com.lijukay.quotesAltDesign.data.model.Screens

private var navigationSelectedItem = mutableIntStateOf(value = 0)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val context: Context = LocalContext.current

    NavigationBar {
        BottomNavigationItem().bottomNavigationItems(context = context).forEachIndexed { index, navigationItem ->
            NavigationBarItem(
                selected = index == navigationSelectedItem.intValue,
                label = { Text(text = navigationItem.label) },
                icon = {
                    Icon(
                        imageVector = navigationItem.icon,
                        contentDescription = navigationItem.label
                    )
                },
                onClick = {
                    navigationSelectedItem.intValue= index
                    navController.navigate(route = navigationItem.route) {
                        popUpTo(id = navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

fun openScreen(route: String, navController: NavController) {
    when (route) {
        Screens.Qwotable.route -> {
            navigationSelectedItem.intValue = 1
            navController.navigate(Screens.Qwotable.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}