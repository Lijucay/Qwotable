package com.lijukay.quotesAltDesign.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme

@Composable
fun FavoriteScreen(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier) {
    QwotableTheme {
        Surface {
            FavoriteQwotableList(qwotableViewModel = qwotableViewModel)
        }
    }
}

@Composable
private fun FavoriteQwotableList(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier) {

}