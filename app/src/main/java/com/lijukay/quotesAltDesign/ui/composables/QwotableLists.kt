package com.lijukay.quotesAltDesign.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QwotableList(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier) {
    val qwotableState = remember { mutableStateOf(emptyList<Qwotable>()) }
    val favoritesState = remember { mutableStateOf(emptyList<Qwotable>()) }

    DisposableEffect(key1 = qwotableViewModel) {
        val observer = Observer { qwotables: List<Qwotable> ->
            qwotableState.value = qwotables
        }
        val observer2 = Observer { qwotables: List<Qwotable> ->
            favoritesState.value = qwotables
        }

        qwotableViewModel.observedFavorites.observeForever(observer2)
        qwotableViewModel.observedQwotables.observeForever(observer)

        onDispose {
            qwotableViewModel.observedFavorites.removeObserver(observer2)
            qwotableViewModel.observedQwotables.removeObserver(observer)
        }
    }

    val rememberQwotableState = remember(qwotableState) { qwotableState }
    val rememberFavoritesState = remember(favoritesState) { favoritesState }

    var currentQwotable: Qwotable? by remember { mutableStateOf(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBSD by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(rememberQwotableState.value) { qwotable: Qwotable ->
            QwotableItemCard(qwotable = qwotable) {
                currentQwotable = qwotable
                showBSD = true
            }
        }
    }

    when {
        showBSD -> {
            ModalBottomSheet(
                onDismissRequest = { showBSD = false },
                sheetState = sheetState
            ) {
                Row(
                    modifier = modifier
                ) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            RichTooltip {
                                if (rememberFavoritesState.value.contains(currentQwotable)) {
                                    Text(
                                        text = stringResource(id = R.string.remove_from_favorites),
                                        modifier = modifier
                                            .padding(8.dp)
                                    )
                                } else {
                                    Text(
                                        text = stringResource(id = R.string.add_to_favorites),
                                        modifier = modifier
                                            .padding(8.dp)
                                    )
                                }
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = {
                            val updatedQwotable = currentQwotable!!.copy(
                                isFavorite = !rememberFavoritesState.value.contains(currentQwotable)
                            )
                            qwotableViewModel.updateQwotable(updatedQwotable)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBSD = false
                                }
                            }
                        }) {
                            Icon(
                                imageVector =
                                if (rememberFavoritesState.value.contains(currentQwotable)) {
                                    Icons.Rounded.Favorite
                                } else {
                                    Icons.Rounded.FavoriteBorder
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                HorizontalDivider()
                Text(
                    text = stringResource(id = R.string.current_qwotable),
                    modifier = modifier.padding(8.dp)
                )
                currentQwotable?.let {
                    QwotableItemCard(qwotable = it, callback = null)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesList(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier) {
    val favoritesState = remember { mutableStateOf(emptyList<Qwotable>()) }

    DisposableEffect(key1 = qwotableViewModel) {
        val observer = Observer { qwotables: List<Qwotable> ->
            favoritesState.value = qwotables
        }

        qwotableViewModel.observedFavorites.observeForever(observer)

        onDispose {
            qwotableViewModel.observedFavorites.removeObserver(observer)
        }
    }

    val rememberFavoritesState = remember(favoritesState) { favoritesState }

    var currentQwotable: Qwotable? by remember { mutableStateOf(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBSD by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(rememberFavoritesState.value) { qwotable: Qwotable ->
            QwotableItemCard(qwotable = qwotable) {
                currentQwotable = qwotable
                showBSD = true
            }
        }
    }

    when {
        showBSD -> {
            ModalBottomSheet(
                onDismissRequest = { showBSD = false },
                sheetState = sheetState
            ) {
                Row(
                    modifier = modifier
                ) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            RichTooltip {
                                Text(
                                    text = stringResource(id = R.string.remove_from_favorites),
                                    modifier = modifier
                                        .padding(8.dp)
                                )
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = {
                            val updatedQwotable = currentQwotable!!.copy(
                                isFavorite = false
                            )
                            qwotableViewModel.updateQwotable(updatedQwotable)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBSD = false
                                }
                            }
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
                    modifier = modifier.padding(8.dp)
                )
                currentQwotable?.let {
                    QwotableItemCard(qwotable = it, callback = null)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnQwotableList(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier) {
    val qwotableState = remember { mutableStateOf(emptyList<Qwotable>()) }

    DisposableEffect(key1 = qwotableViewModel) {
        val observer = Observer { qwotables: List<Qwotable> ->
            qwotableState.value = qwotables
        }

        qwotableViewModel.observedOwn.observeForever(observer)

        onDispose {
            qwotableViewModel.observedOwn.removeObserver(observer)
        }
    }

    val rememberQwotableState = remember(qwotableState) { qwotableState }

    var currentQwotable: Qwotable? by remember { mutableStateOf(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBSD by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(rememberQwotableState.value) { qwotable: Qwotable ->
            QwotableItemCard(qwotable = qwotable) {
                currentQwotable = qwotable
                showBSD = true
            }
        }
    }

    when {
        showBSD -> {
            ModalBottomSheet(
                onDismissRequest = { showBSD = false },
                sheetState = sheetState
            ) {
                Row(
                    modifier = modifier
                ) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            RichTooltip {
                                Text(
                                    text = stringResource(id = R.string.edit_qwotable),
                                    modifier = modifier
                                        .padding(8.dp)
                                )
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = {
                            //Update qwotable
                            //qwotableViewModel.updateQwotable(updatedQwotable)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBSD = false
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null,
                            )
                        }
                    }
                }
                HorizontalDivider()
                Text(
                    text = stringResource(id = R.string.current_qwotable),
                    modifier = modifier.padding(horizontal = 8.dp)
                )
                currentQwotable?.let {
                    QwotableItemCard(qwotable = it, callback = null)
                }
            }
        }
    }
}