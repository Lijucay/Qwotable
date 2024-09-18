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

package com.lijukay.quotesAltDesign.data.utils

sealed class Screens(val route: String) {
    data object MainScreen : Screens("main_screen")
    data object SettingsScreen : Screens("settings_screen")
    data object LicensesScreen : Screens("licenses_screen")

    data object QwotableScreen : Screens("qwotable_screen")
    data object HomeScreen : Screens("home_screen")
    data object OwnQwotableScreen : Screens("own_qwotable_screen")
}