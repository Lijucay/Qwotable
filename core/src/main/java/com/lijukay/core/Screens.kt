package com.lijukay.core

sealed class Screens(val route: String) {
    object Home : Screens("home_route")
    object Favorite : Screens("favorites_route")
    object Qwotable : Screens("qwotable_route")
    object OwnQwotables : Screens("own_qwotables_route")
}