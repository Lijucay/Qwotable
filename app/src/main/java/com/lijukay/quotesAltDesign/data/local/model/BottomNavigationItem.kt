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

package com.lijukay.quotesAltDesign.data.local.model

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.utils.Screens

/**
 * The data about a bottom navigation icon
 * @param label Text, that shows the current name of the screen, shown underneath the icon
 * @param filledIcon Icon, that shows when the item is selected
 * @param outlinedIcon Icon, that shows when the item is unselected
 * @param route Route for the NavHostController to navigate to the appropriate screen
 * */
data class BottomNavigationItem(
    val label: String = "",
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val route: String = ""
) {
    companion object {
        /**
         * Function that returns a list of [BottomNavigationItem]
         * @param context The current activity context
         * */
        fun bottomNavigationItems(context: Context): List<BottomNavigationItem> {
            return listOf(
                BottomNavigationItem(
                    label = context.getString(R.string.qwotables),
                    outlinedIcon = Icons.Rounded.StarOutline,
                    filledIcon = Icons.Rounded.Star,
                    route = Screens.QwotableScreen.route
                ),
                BottomNavigationItem(
                    label = context.getString(R.string.home),
                    outlinedIcon = Icons.Outlined.Home,
                    filledIcon = Icons.Rounded.Home,
                    route = Screens.HomeScreen.route
                ),
                BottomNavigationItem(
                    label = context.getString(R.string.own),
                    outlinedIcon = Icons.Rounded.PersonOutline,
                    filledIcon = Icons.Rounded.Person,
                    route = Screens.OwnQwotableScreen.route
                )
            )
        }
    }
}