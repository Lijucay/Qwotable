package com.lijukay.quotesAltDesign.model

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.lijukay.core.Screens
import com.lijukay.quotesAltDesign.R

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Rounded.Home,
    val route: String = ""
) {
    fun bottomNavigationItems(context: Context): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = context.getString(R.string.home),
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
                label = context.getString(R.string.my_qwotable),
                icon = Icons.Rounded.Person,
                route = Screens.OwnQwotables.route
            )
        )
    }
}