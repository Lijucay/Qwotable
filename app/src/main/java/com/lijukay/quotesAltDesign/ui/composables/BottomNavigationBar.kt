package com.lijukay.quotesAltDesign.ui.composables

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
import com.lijukay.quotesAltDesign.model.BottomNavigationItem

@Composable
fun BottomNavigationBar(navController: NavController) {
    var navigationSelectedItem: Int by remember { mutableIntStateOf(value = 0) }
    val context: Context = LocalContext.current

    NavigationBar {
        BottomNavigationItem().bottomNavigationItems(context = context).forEachIndexed { index, navigationItem ->
            NavigationBarItem(
                selected = index == navigationSelectedItem,
                label = { Text(text = navigationItem.label) },
                icon = {
                    Icon(
                        imageVector = navigationItem.icon,
                        contentDescription = navigationItem.label
                    )
                },
                onClick = {
                    navigationSelectedItem = index
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