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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryItemCard(library: Library, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = modifier
            .padding(all = 8.dp)
            .fillMaxWidth(),
        onClick = {
            library.website?.let { uriHandler.openUri(it) }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 6.dp),
            modifier = modifier.padding(all = 16.dp)
        ) {
            Text(
                text = library.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            if (!library.description.isNullOrBlank()) {
                Text(
                    text = library.description!!,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            FlowRow(
                modifier = modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(space = 6.dp),
                verticalArrangement = Arrangement.spacedBy(space = 6.dp)
            ) {
                if (!library.artifactVersion.isNullOrBlank()) {
                    ExtraInfoTexts(text = "Version: ${library.artifactVersion}")
                }
                library.developers.forEach { developer: Developer ->
                    if (!developer.name.isNullOrBlank()) {
                        ExtraInfoTexts(text = developer.name!!)
                    }
                }
                library.licenses.forEach { license: License ->
                    ExtraInfoTexts(text = license.name)
                }
                if (library.openSource) {
                    OpenSourceInfo()
                }
            }
        }
    }
}

@Composable
fun ExtraInfoTexts(modifier: Modifier = Modifier, text: String) {
    Card(
        shape = RoundedCornerShape(percent = 100),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = text,
            modifier = modifier.padding(vertical = 6.dp, horizontal = 10.dp)
        )
    }
}

@Composable
fun OpenSourceInfo(modifier: Modifier = Modifier) {
    OutlinedCard(
        shape = RoundedCornerShape(percent = 100),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "Open Source",
            modifier = modifier.padding(vertical = 6.dp, horizontal = 10.dp)
        )
    }
}
