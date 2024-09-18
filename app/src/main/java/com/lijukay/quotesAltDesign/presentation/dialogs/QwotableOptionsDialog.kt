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

package com.lijukay.quotesAltDesign.presentation.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.local.model.DBQwotable
import com.lijukay.quotesAltDesign.data.local.model.LocalQwotable
import com.lijukay.quotesAltDesign.data.local.model.OwnQwotable
import com.lijukay.quotesAltDesign.presentation.composables.QwotableItemCard
import com.lijukay.quotesAltDesign.presentation.viewmodels.QwotableViewModel
import com.lijukay.quotesAltDesign.domain.util.ClipboardUtil.copyToClipboard
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey
import com.lijukay.quotesAltDesign.domain.util.ShareUtil.share
import com.lijukay.quotesAltDesign.domain.util.dataStore
import com.lijukay.quotesAltDesign.domain.util.states.SharePreferences
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QwotableOptionsDialog(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    qwotableViewModel: QwotableViewModel,
    currentLocalQwotable: LocalQwotable,
    onEditingRequest: () -> Unit,
    onDeletionRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        val context = LocalContext.current
        var isFavorite by remember { mutableStateOf(currentLocalQwotable.isFavorite) }
        val showEditorOptions = currentLocalQwotable is OwnQwotable

        val sharePreference by context.dataStore.data.map { preferences ->
            preferences[PreferenceKey.SHARE_PREFERENCE_KEY] ?: SharePreferences.EVERYTHING.ordinal
        }.collectAsState(SharePreferences.EVERYTHING.ordinal)

        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            if (currentLocalQwotable is DBQwotable) {
                IconButton(onClick = {
                    val updatedQwotable = currentLocalQwotable.copy(isFavorite = !isFavorite)
                    isFavorite = !isFavorite
                    qwotableViewModel.updateQwotable(updatedQwotable)
                }) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Rounded.Favorite
                        } else {
                            Icons.Rounded.FavoriteBorder
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (showEditorOptions) {
                IconButton(
                    onClick = {
                        onEditingRequest()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = {
                        onDeletionRequest()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null
                    )
                }
            }

            IconButton(
                onClick = {
                    copyToClipboard(context, currentLocalQwotable.quote)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = null
                )
            }

            IconButton(
                onClick = {
                    currentLocalQwotable.share(context, sharePreference)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = null
                )
            }
        }

        HorizontalDivider()

        Text(
            text = stringResource(id = R.string.current_qwotable),
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        )

        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            QwotableItemCard(localQwotable = currentLocalQwotable, onClick = null)
        }
    }
}