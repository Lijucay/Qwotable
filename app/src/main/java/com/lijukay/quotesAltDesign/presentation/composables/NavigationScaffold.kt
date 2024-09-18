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

package com.lijukay.quotesAltDesign.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.lijukay.quotesAltDesign.data.local.model.BottomNavigationItem.Companion.bottomNavigationItems
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel

@Composable
fun NavigationScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiViewModel: UIViewModel,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val selectedScreenIndex by uiViewModel.selectedScreenIndex.collectAsState()

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            bottomNavigationItems(context).forEachIndexed { index, item ->
                item(
                    selected = index == selectedScreenIndex,
                    label = {
                        AnimatedVisibility(
                            visible = index == selectedScreenIndex,
                            enter = expandVertically(expandFrom = Alignment.Top),
                            exit = shrinkVertically(shrinkTowards = Alignment.Top)
                        ) {
                            Text(item.label)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedScreenIndex) {
                                item.filledIcon
                            } else {
                                item.outlinedIcon
                            },
                            contentDescription = item.label
                        )
                    },
                    onClick = {
                        uiViewModel.setSelectedScreenIndex(index)
                        navController.navigate(route = item.route) {
                            popUpTo(id = navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        content = content
    )
}