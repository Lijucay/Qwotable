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

package com.lijukay.quotesAltDesign.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lijukay.quotesAltDesign.presentation.composables.QwotableItemCard
import com.lijukay.quotesAltDesign.presentation.viewmodels.QwotableViewModel
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel

@Composable
fun OwnQwotablesScreen(
    qwotableViewModel: QwotableViewModel,
    uiViewModel: UIViewModel
) {
    uiViewModel.setSelectedScreenIndex(2)
    val qwotableList by qwotableViewModel.ownQwotables.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = qwotableList, key = { qwotable -> qwotable.id }) { qwotable ->
            QwotableItemCard(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .animateItem(),
                localQwotable = qwotable
            ) {
                uiViewModel.setCurrentSelectedQwotable(qwotable)
                uiViewModel.setShowQwotableOptionsDialog(true)
            }
        }
    }
}