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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.local.model.LocalQwotable
import com.lijukay.quotesAltDesign.data.local.model.OwnQwotable
import com.lijukay.quotesAltDesign.data.shared.Qwotable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialog(
    sheetState: SheetState,
    localQwotable: OwnQwotable,
    onEditQwotable: (OwnQwotable) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var qwotableQuote by remember { mutableStateOf(localQwotable.quote) }
    var qwotableAuthor by remember { mutableStateOf(localQwotable.author) }
    var qwotableSource by remember { mutableStateOf(localQwotable.source) }

    var saveButtonEnabled by remember { mutableStateOf(true) }
    var quoteError by remember { mutableStateOf(false) }
    var quoteErrorMessage by remember { mutableStateOf(context.getString(R.string.quote_cannot_be_empty)) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onDismissRequest,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
            }

            Text(
                text = stringResource(id = R.string.edit_qwotable),
                fontWeight = FontWeight.Bold
            )

            IconButton(
                enabled = saveButtonEnabled,
                onClick = {
                    onEditQwotable(
                        OwnQwotable(
                            id = localQwotable.id,
                            quote = qwotableQuote,
                            author = qwotableAuthor,
                            source = qwotableSource
                        )
                    )
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Rounded.Save,
                    contentDescription = null
                )
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            OutlinedTextField(
                value = qwotableQuote,
                onValueChange = { input ->
                    qwotableQuote = input

                    if (input.isEmpty()) {
                        quoteError = true
                        saveButtonEnabled = false
                        quoteErrorMessage = context.getString(R.string.quote_cannot_be_empty)
                    } else {
                        quoteError = false
                        saveButtonEnabled = true
                    }
                },
                label = { Text(text = stringResource(id = R.string.quote)) },
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                supportingText = {
                    AnimatedVisibility(visible = quoteError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = quoteErrorMessage,
                            textAlign = TextAlign.End
                        )
                    }
                }
            )

            OutlinedTextField(
                value = qwotableAuthor,
                onValueChange = { qwotableAuthor = it },
                label = { Text(text = stringResource(id = R.string.author)) },
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            )

            OutlinedTextField(
                value = qwotableSource,
                onValueChange = { qwotableSource = it },
                label = { Text(text = stringResource(id = R.string.source)) },
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            )
        }
    }
}