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

package com.lijukay.quotesAltDesign.ui.navigation.screens

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.ui.composables.lists.OwnQwotableList

@Composable
fun OwnQwotablesScreen(qwotableViewModel: QwotableViewModel, uiViewModel: UIViewModel) {
        Surface {
            OwnQwotableList(qwotableViewModel = qwotableViewModel, uiViewModel = uiViewModel)
        }
    }