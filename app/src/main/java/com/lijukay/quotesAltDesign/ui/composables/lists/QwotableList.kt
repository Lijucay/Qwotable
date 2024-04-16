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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Observer
import com.lijukay.core.database.Qwotable
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.core.R
import com.lijukay.quotesAltDesign.data.StringValue
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.ui.composables.item_cards.QwotableItemCard

@Composable
fun QwotableList(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier, uiViewModel: UIViewModel) {
    val context = LocalContext.current
    val qwotableState = remember { mutableStateOf(value = emptyList<Qwotable>()) }
    val currentLanguage = remember {
        mutableStateOf(StringValue.Default.asString(context)
            .ifEmpty { context.getString(R.string.default_language) }
        )
    }

    LaunchedEffect(key1 = Unit) {
        uiViewModel.selectedLanguage.value?.let {
            currentLanguage.value = it.asString(context)
        }

        val observer = Observer { qwotables: List<Qwotable> ->
            qwotableState.value = qwotables
        }
        qwotableViewModel.getFilteredQwotable(currentLanguage.value).observeForever(observer)
    }

    DisposableEffect(key1 = listOf(qwotableViewModel, uiViewModel)) {
        val observer = Observer { qwotables: List<Qwotable> ->
            qwotableState.value = qwotables
        }
        val languageObserver = Observer<StringValue> { language ->
            currentLanguage.value = language.asString(context)
            qwotableViewModel.getFilteredQwotable(language.asString(context)).observeForever(observer)
        }

        qwotableViewModel.getFilteredQwotable(currentLanguage.value).observeForever(observer)

        uiViewModel.selectedLanguage.observeForever(languageObserver)

        onDispose {
            qwotableViewModel.getFilteredQwotable(currentLanguage.value).removeObserver(observer)
            uiViewModel.selectedLanguage.removeObserver(languageObserver)
        }
    }

    val rememberQwotableState = remember(key1 = qwotableState) { qwotableState }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = rememberQwotableState.value) { qwotable: Qwotable ->
            QwotableItemCard(qwotable = qwotable) {
                uiViewModel.setCurrentSelectedQwotable(qwotable)
                uiViewModel.setShowQwotableOptionsBottomSheet(true)
            }
        }
    }
}