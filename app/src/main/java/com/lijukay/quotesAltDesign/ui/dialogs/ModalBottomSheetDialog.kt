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

package com.lijukay.quotesAltDesign.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lijukay.core.R
import com.lijukay.core.database.Qwotable
import com.lijukay.core.utils.ClipboardUtil.Companion.copyToClipboard
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.core.utils.ShareUtil.Companion.shareExternally
import com.lijukay.quotesAltDesign.ui.composables.item_cards.QwotableItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    showEditorOptions: Boolean,
    currentQwotable: Qwotable,
    qwotableViewModel: QwotableViewModel,
    onEditingRequest: (Qwotable) -> Unit,
    onDeletionRequest: (Qwotable) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        windowInsets = WindowInsets.displayCutout.exclude(WindowInsets.safeDrawing),
        sheetState = sheetState
    ) {
        val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val isFavorite = remember { mutableStateOf(currentQwotable.isFavorite) }
        val context = LocalContext.current
        val composeView: @Composable () -> Unit =
            { QwotableItemCard(qwotable = currentQwotable, onClick = null) }

        Column(
            modifier = modifier.padding(bottom = bottomPadding),
        ) {
            Row(modifier = modifier) {
                IconButton(onClick = {
                    val updateQwotable = currentQwotable.copy(isFavorite = !isFavorite.value)
                    isFavorite.value = !isFavorite.value
                    qwotableViewModel.updateQwotable(updateQwotable)
                    //onDismissRequest()
                }) {
                    if (isFavorite.value) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                if (showEditorOptions) {
                    IconButton(onClick = {
                        onEditingRequest(currentQwotable)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = {
                        onDeletionRequest(currentQwotable)
                    }) {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                    }
                }
                IconButton(
                    onClick = {
                        currentQwotable.qwotable.copyToClipboard(context)
                        Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = null)
                }
                IconButton(
                    onClick = {
                        currentQwotable.qwotable.shareExternally(context)
                    }
                ) {
                    Icon(imageVector = Icons.Rounded.Share, contentDescription = null)
                }
            }
            HorizontalDivider()
            Text(
                text = stringResource(id = R.string.current_qwotable),
                modifier = modifier.padding(all = 8.dp)
            )

            val scrollState = rememberScrollState()
            Box(
                modifier = modifier.verticalScroll(state = scrollState)
            ) {
                composeView()
            }
        }
    }
}