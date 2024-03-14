package com.lijukay.quotesAltDesign.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.ui.composables.FavoritesList
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme

@Composable
fun FavoriteScreen(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier) {
    QwotableTheme {
        Surface(
            modifier = modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FavoritesList(qwotableViewModel = qwotableViewModel)
        }
    }
}