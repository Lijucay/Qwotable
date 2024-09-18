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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey
import com.lijukay.quotesAltDesign.domain.util.dataStore
import com.lijukay.quotesAltDesign.presentation.composables.QwotableItemCard
import com.lijukay.quotesAltDesign.presentation.composables.RandomQuoteCard
import com.lijukay.quotesAltDesign.presentation.composables.RotatableArrow
import com.lijukay.quotesAltDesign.presentation.viewmodels.QwotableViewModel
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel
import kotlinx.coroutines.flow.map

@Composable
fun HomeScreen(
    uiViewModel: UIViewModel,
    qwotableViewModel: QwotableViewModel
) {
    uiViewModel.setSelectedScreenIndex(1)

    val context = LocalContext.current

    val favoritesList by qwotableViewModel.favoriteQwotables.collectAsState()
    val randomQuote by qwotableViewModel.randomQuote.collectAsState()

    var preferenceLoaded by remember { mutableStateOf(false) }
    val showRandomQuoteCard by context.dataStore.data
        .map { preferences -> preferences[PreferenceKey.SHOW_RANDOM_QUOTES] ?: true }
        .collectAsState(initial = true)

    LaunchedEffect(key1 = showRandomQuoteCard) {
        preferenceLoaded = true
    }

    val showFavorites by uiViewModel.showFavoriteQuotes.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            randomQuote?.let {
                RandomQuoteCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    randomQuote = it,
                    uiViewModel = uiViewModel,
                    onLoadNewRandomQuote = {
                        uiViewModel.setLoading(true)
                        qwotableViewModel.getRandomQuote().invokeOnCompletion {
                            uiViewModel.setLoading(false)
                        }
                    },
                    showCard = (showRandomQuoteCard && preferenceLoaded),
                    onShowInfoClicked = {
                        uiViewModel.setInfoDialogTitle(context.getString(R.string.about_random_quote_title))
                        uiViewModel.setInfoDialogMessage(context.getString(R.string.about_random_quote))
                        uiViewModel.setInfoDialogAction(null)
                        uiViewModel.setShowInfoDialog(true)
                    }
                )
            }
        }

        item {
            RotatableArrow(isExpanded = showFavorites) {
                uiViewModel.setShowFavorites(!showFavorites)
            }
        }

        items(favoritesList) { qwotable ->
            QwotableItemCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                localQwotable = qwotable,
                visible = showFavorites
            ) {
                uiViewModel.setCurrentSelectedQwotable(qwotable)
                uiViewModel.setShowQwotableOptionsDialog(true)
            }
        }
    }
}