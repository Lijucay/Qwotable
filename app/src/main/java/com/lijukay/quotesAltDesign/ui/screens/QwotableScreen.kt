package com.lijukay.quotesAltDesign.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import com.lijukay.core.database.Qwotable
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.ui.composables.QwotableItemCard
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme


@Composable
fun QwotableScreen(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier) {
    QwotableTheme {
        Surface(
            modifier = modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            QwotableList(qwotableViewModel = qwotableViewModel)
        }
    }
}


@Composable
private fun QwotableList(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier) {
    val qwotableState = remember { mutableStateOf(emptyList<Qwotable>()) }

    DisposableEffect(key1 = qwotableViewModel) {
        val observer = Observer { qwotables: List<Qwotable> ->
            qwotableState.value = qwotables
        }

        qwotableViewModel.observedQwotables.observeForever(observer)

        onDispose {
            qwotableViewModel.observedQwotables.removeObserver(observer)
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
