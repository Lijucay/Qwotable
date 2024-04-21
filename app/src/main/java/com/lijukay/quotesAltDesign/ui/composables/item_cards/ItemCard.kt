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

package com.lijukay.quotesAltDesign.ui.composables.item_cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lijukay.core.R
import com.lijukay.core.database.Qwotable

@Composable
fun QwotableItemCard(
    modifier: Modifier = Modifier,
    qwotable: Qwotable,
    visible: MutableState<Boolean> = mutableStateOf(true),
    onClick: (() -> Unit)?
) {
    val qwotableQuote = qwotable.qwotable
    val qwotableAuthor = qwotable.author.ifEmpty { stringResource(id = R.string.unknown) }
    val qwotableSource = qwotable.source.ifEmpty { stringResource(id = R.string.unknown) }

    AnimatedVisibility(
        visible = visible.value,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = modifier
                .padding(all = 8.dp)
                .fillMaxWidth(),
            onClick = {
                onClick?.let { callback -> callback() }
            }
        ) {
            Text(
                text = qwotableQuote,
                modifier = modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp
                    )
                    .fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = qwotableAuthor,
                modifier = modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp
                    )
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = qwotableSource,
                modifier = modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp
                    )
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

