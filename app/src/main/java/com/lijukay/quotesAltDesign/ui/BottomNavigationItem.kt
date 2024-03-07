package com.lijukay.quotesAltDesign.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.lijukay.core.Screens

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Home,
    val route: String = ""
) {
    fun bottomNavigationIcons(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Home",
                icon = Icons.Filled.Home,
                route = Screens.Home.route
            ),
            BottomNavigationItem(
                label = "Favorites",
                icon = Icons.Filled.Favorite,
                route = Screens.Favorite.route
            ),
            BottomNavigationItem(
                label = "My Qwotables",
                icon = Icons.Filled.Person,
                route = Screens.OwnQwotables.route
            )
        )
    }
}