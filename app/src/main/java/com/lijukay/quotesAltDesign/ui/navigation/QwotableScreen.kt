package com.lijukay.quotesAltDesign.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.ui.composables.QwotableList
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
