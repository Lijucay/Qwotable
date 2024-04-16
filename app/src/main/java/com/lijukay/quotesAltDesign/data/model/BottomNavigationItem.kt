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

package com.lijukay.quotesAltDesign.data.model

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.lijukay.core.R

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Rounded.Home,
    val route: String = ""
) {
    fun bottomNavigationItems(context: Context): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = context.getString(R.string.sanctuary),
                icon = Icons.Rounded.Home,
                route = Screens.Home.route
            ),
            BottomNavigationItem(
                label = context.getString(R.string.qwotable),
                icon = Icons.Rounded.Star,
                route = Screens.Qwotable.route
            ),
            BottomNavigationItem(
                label = context.getString(R.string.favorites),
                icon = Icons.Rounded.Favorite,
                route = Screens.Favorite.route
            ),
            BottomNavigationItem(
                label = context.getString(R.string.mine),
                icon = Icons.Rounded.Person,
                route = Screens.OwnQwotables.route
            )
        )
    }
}