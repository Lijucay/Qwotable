package com.lijukay.quotesAltDesign.ui.composables.lists

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.lijukay.core.database.Qwotable
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.ui.composables.QwotableItemCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesList(modifier: Modifier = Modifier ,qwotableViewModel: QwotableViewModel) {
    val favoritesState = remember { mutableStateOf(value = emptyList<Qwotable>()) }

    DisposableEffect(key1 = qwotableViewModel) {
        val observer = Observer { qwotables: List<Qwotable> ->
            favoritesState.value = qwotables
        }

        qwotableViewModel.observedFavorites.observeForever(observer)

        onDispose { qwotableViewModel.observedFavorites.removeObserver(observer) }
    }

    val rememberFavoritesState = remember(key1 = favoritesState) { favoritesState }
    var currentQwotable: Qwotable? by remember { mutableStateOf(value = null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBSD by remember { mutableStateOf(value = false) }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = rememberFavoritesState.value) { qwotable: Qwotable ->
            QwotableItemCard(qwotable = qwotable) {
                currentQwotable = qwotable
                showBSD = true
            }
        }
    }

    if (showBSD) {
        ModalBottomSheet(
            onDismissRequest = { showBSD = false },
            sheetState = sheetState
        ) {
            Row(modifier = modifier) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    tooltip = {
                        RichTooltip {
                            Text(
                                text = stringResource(id = R.string.remove_from_favorites),
                                modifier = modifier.padding(all = 8.dp)
                            )
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    IconButton(onClick = {
                        val updatedQwotable = currentQwotable!!.copy(isFavorite = false)
                        qwotableViewModel.updateQwotable(qwotable = updatedQwotable)
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion { if (!sheetState.isVisible) showBSD = false }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            HorizontalDivider()
            Text(
                text = stringResource(id = R.string.current_qwotable),
                modifier = modifier.padding(all = 8.dp)
            )
            currentQwotable?.let {
                QwotableItemCard(qwotable = it, onClick = null)
            }
        }
    }
}