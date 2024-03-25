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

package com.lijukay.quotesAltDesign.ui.composables.lists

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import com.lijukay.core.database.Qwotable
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.ui.composables.item_cards.QwotableItemCard

@Composable
fun FavoritesList(
    modifier: Modifier = Modifier,
    qwotableViewModel: QwotableViewModel,
    uiViewModel: UIViewModel
) {
    val favoritesList = remember { mutableStateOf(emptyList<Qwotable>()) }

    DisposableEffect(key1 = listOf(qwotableViewModel, uiViewModel)) {
        val favoritesObserver = Observer<List<Qwotable>> { favorites ->
            favoritesList.value = favorites
        }

        qwotableViewModel.observedFavorites.observeForever(favoritesObserver)

        onDispose {
            qwotableViewModel.observedFavorites.removeObserver(favoritesObserver)
        }
    }

    DisposableEffect(key1 = qwotableViewModel) {
        val observer = Observer { qwotables: List<Qwotable> ->
            favoritesList.value = qwotables
        }

        qwotableViewModel.observedFavorites.observeForever(observer)

        onDispose { qwotableViewModel.observedFavorites.removeObserver(observer) }
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = favoritesList.value) { qwotable: Qwotable ->
            QwotableItemCard(qwotable = qwotable) {
                uiViewModel.setCurrentSelectedQwotable(qwotable)
                uiViewModel.setShowQwotableOptionsBottomSheet(true)
            }
        }
    }
}