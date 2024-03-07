package com.lijukay.quotesAltDesign.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.lijukay.core.Screens

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Rounded.Home,
    val route: String = ""
) {
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Home",
                icon = Icons.Rounded.Home,
                route = Screens.Home.route
            ),
            BottomNavigationItem(
                label = "Qwotables",
                icon = Icons.Rounded.Star,
                route = Screens.Qwotable.route
            ),
            BottomNavigationItem(
                label = "Favorites",
                icon = Icons.Rounded.Favorite,
                route = Screens.Favorite.route
            ),
            BottomNavigationItem(
                label = "My Qwotables",
                icon = Icons.Rounded.Person,
                route = Screens.OwnQwotables.route
            )
        )
    }
}