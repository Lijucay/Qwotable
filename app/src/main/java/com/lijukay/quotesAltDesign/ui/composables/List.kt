package com.lijukay.quotesAltDesign.ui.composables

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

@Composable
fun List(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier, type: String = "all") {
    val qwotableState = remember { mutableStateOf(emptyList<Qwotable>()) }

    DisposableEffect(key1 = qwotableViewModel) {
        val observer = Observer { qwotables: List<Qwotable> ->
            qwotableState.value = qwotables
        }

        when (type) {
            "all" -> qwotableViewModel.observedQwotables.observeForever(observer)
            "favorite" -> qwotableViewModel.observedFavorites.observeForever(observer)
            "own" -> qwotableViewModel.observedOwn.observeForever(observer)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        onDispose {
            when (type) {
                "all" -> qwotableViewModel.observedQwotables.removeObserver(observer)
                "favorite" -> qwotableViewModel.observedFavorites.removeObserver(observer)
                "own" -> qwotableViewModel.observedOwn.removeObserver(observer)
                else -> throw IllegalArgumentException("Unknown type: $type")
            }
        }
    }

    val rememberQwotableState = remember(qwotableState) { qwotableState }

    LazyColumn(modifier = modifier
        .fillMaxSize()
    ) {
        items(rememberQwotableState.value) { qwotable: Qwotable ->
            QwotableItemCard(qwotable = qwotable)
        }
    }
}