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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lijukay.core.R
import com.lijukay.core.database.Qwotable
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.data.UIViewModel

@Composable
fun AddEditDialog(
    modifier: Modifier = Modifier,
    qwotableViewModel: QwotableViewModel,
    uiViewModel: UIViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val item = remember { mutableStateOf<Qwotable?>(null) }
    val scrollState = rememberScrollState()
    var qwotableQuote by remember { mutableStateOf("") }
    var qwotableAuthor by remember { mutableStateOf("") }
    var qwotableSource by remember { mutableStateOf("") }

    var saveButtonEnabled by remember { mutableStateOf(false) }
    var quoteError by remember { mutableStateOf(false) }

    val quoteErrorMessage by remember { mutableStateOf(context.getString(R.string.quote_cannot_be_empty)) }
    val action: () -> Unit = {
        if (item.value != null) {
            val updatedItem = item.value!!.copy(
                qwotable = qwotableQuote,
                author = qwotableAuthor,
                source = qwotableSource
            )
            qwotableViewModel.updateQwotable(updatedItem)
            onDismissRequest()
        } else {
            val newItem = Qwotable(
                qwotable = qwotableQuote.trim(),
                author = qwotableAuthor.trim(),
                source = qwotableSource.trim(),
                language = "",
                isOwn = true
            )
            qwotableViewModel.insert(newItem)
            onDismissRequest()
        }
    }

    LaunchedEffect(key1 = Unit) {
        item.value = uiViewModel.currentSelectedQwotable.value

        item.value.run {
            qwotableQuote = this?.qwotable.orEmpty()
            qwotableAuthor = this?.author.orEmpty()
            qwotableSource = this?.source.orEmpty()

            quoteError = (this == null)
            saveButtonEnabled = (this != null)
        }
    }

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        ElevatedCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    vertical = 36.dp,
                    horizontal = 5.dp
                ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = if (item.value != null) {
                    stringResource(id = R.string.edit_qwotable)
                } else {
                    stringResource(id = R.string.create_qwotable)
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = modifier
                    .verticalScroll(state = scrollState)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = qwotableQuote,
                    onValueChange = { input: String ->
                        qwotableQuote = input
                        if (input.isBlank()) {
                            quoteError = true
                            saveButtonEnabled = false
                        } else {
                            quoteError = false
                            saveButtonEnabled = true
                        }
                    },
                    label = {
                        Text(text = stringResource(id = R.string.quote))
                    },
                    modifier = modifier
                        .height(250.dp)
                        .padding(8.dp),
                    isError = quoteError,
                    supportingText = {
                        AnimatedVisibility(visible = quoteError) {
                            Text(
                                modifier = modifier.fillMaxWidth(),
                                text = quoteErrorMessage,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                )
                OutlinedTextField(
                    value = qwotableAuthor,
                    onValueChange = { input: String -> qwotableAuthor = input },
                    label = {
                        Text(text = stringResource(id = R.string.author))
                    },
                    modifier = modifier.padding(8.dp)
                )
                OutlinedTextField(
                    value = qwotableSource,
                    onValueChange = { input: String -> qwotableSource = input },
                    label = { Text(text = stringResource(id = R.string.source)) },
                    modifier = modifier.padding(8.dp)
                )

                Row(
                    modifier.padding(8.dp)
                ) {
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = modifier
                            .weight(1f)
                            .padding(end = 2.dp)
                    ) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                    Button(
                        onClick = action,
                        modifier = modifier
                            .weight(1f)
                            .padding(start = 2.dp),
                        enabled = saveButtonEnabled
                    ) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
            }
        }
    }
}