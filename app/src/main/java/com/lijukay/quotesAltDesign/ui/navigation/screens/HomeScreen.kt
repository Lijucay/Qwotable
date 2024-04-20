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

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.lijukay.core.R
import com.lijukay.core.database.Qwotable
import com.lijukay.core.utils.ClipboardUtil.Companion.copyToClipboard
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.core.utils.ShareUtil.Companion.shareExternally
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.ui.composables.item_cards.QwotableItemCard
import com.lijukay.quotesAltDesign.ui.composables.widgets.RotatableArraw

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiViewModel: UIViewModel,
    qwotableViewModel: QwotableViewModel
) {
    val favoritesList = remember { mutableStateOf(emptyList<Qwotable>()) }
    val isLoading = remember { mutableStateOf(false) }
    val randomQuote = remember { mutableStateOf("") }
    val context = LocalContext.current
    val showFavorites = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        uiViewModel.getRandomQuote(context)
    }

    DisposableEffect(key1 = uiViewModel) {
        val loadingObserver = Observer<Boolean> { isLoadingQuote ->
            isLoading.value = isLoadingQuote
        }
        val randomQuoteObserver = Observer<String> { quote ->
            randomQuote.value = quote
        }

        uiViewModel.isLoadingRandomQuote.observeForever(loadingObserver)
        uiViewModel.randomQuote.observeForever(randomQuoteObserver)

        onDispose {
            uiViewModel.isLoadingRandomQuote.removeObserver(loadingObserver)
            uiViewModel.randomQuote.removeObserver(randomQuoteObserver)
        }
    }

    DisposableEffect(key1 = listOf(qwotableViewModel, uiViewModel)) {
        val favoritesObserver = Observer<List<Qwotable>> { favorites ->
            favoritesList.value = favorites
        }

        qwotableViewModel.observedFavorites.observeForever(favoritesObserver)

        onDispose { qwotableViewModel.observedFavorites.removeObserver(favoritesObserver) }
    }

    DisposableEffect(key1 = qwotableViewModel) {
        val observer = Observer { qwotables: List<Qwotable> -> favoritesList.value = qwotables }

        qwotableViewModel.observedFavorites.observeForever(observer)

        onDispose { qwotableViewModel.observedFavorites.removeObserver(observer) }
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Card(
                modifier = modifier
                    .padding(all = 8.dp)
                    .animateContentSize()
                    .fillMaxWidth()
            ) {
                Text(
                    text = randomQuote.value,
                    modifier = modifier
                        .animateContentSize()
                        .padding(all = 16.dp)
                )
                FlowRow(
                    modifier = modifier
                        .padding(start = 16.dp, end = 8.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            uiViewModel.getRandomQuote(context)
                        },
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                modifier = modifier.size(size = 16.dp),
                                color = MaterialTheme.colorScheme.onSecondary,
                                strokeWidth = 3.dp,
                                strokeCap = StrokeCap.Round
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.renew),
                            modifier = modifier.padding(start = if (isLoading.value) 8.dp else 0.dp)
                        )
                    }

                    Row {
                        IconButton(onClick = {
                            randomQuote.value.copyToClipboard(context)
                            Toast.makeText(
                                context,
                                context.getString(R.string.copied),
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = {
                            randomQuote.value.shareExternally(context)
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = {
                            uiViewModel.setShowInformationDialog(true)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }

        item {
            RotatableArraw(isExpanded = showFavorites) {
                showFavorites.value = !showFavorites.value
            }
        }

        items(items = favoritesList.value) { qwotable: Qwotable ->
            QwotableItemCard(qwotable = qwotable, visible = showFavorites) {
                uiViewModel.setCurrentSelectedQwotable(qwotable)
                uiViewModel.setShowQwotableOptionsBottomSheet(true)
            }
        }
    }
}
