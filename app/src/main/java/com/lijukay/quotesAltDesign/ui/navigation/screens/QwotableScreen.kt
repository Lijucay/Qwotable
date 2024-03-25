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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.ui.composables.lists.QwotableList
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme

@Composable
fun QwotableScreen(
    modifier: Modifier = Modifier,
    qwotableViewModel: QwotableViewModel,
    uiViewModel: UIViewModel
) {
    QwotableTheme {
        Surface(
            modifier = modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            QwotableList(qwotableViewModel = qwotableViewModel, uiViewModel = uiViewModel)
        }
    }
}
