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

package com.lijukay.quotesAltDesign.presentation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.presentation.composables.LibraryItemCard
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.entity.Scm
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(
    onNavigateBack: () -> Unit
) {
    val libs = Libs
        .Builder()
        .withContext(LocalContext.current)
        .build()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.licenses))
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        val licenses = mutableListOf<Library>()

        val customLibraries: List<Library> = listOf(
            Library(
                uniqueId = "",
                artifactVersion = null,
                name = "kanye.rest API",
                description = "A free REST API for random Kanye West quotes (Kanye as a Service).\nBuilt with Cloudflare Workers.",
                website = "https://github.com/ajzbc/kanye.rest",
                developers = persistentListOf(Developer(name = "Andrew Jazbec", organisationUrl = null)),
                organization = null,
                scm = Scm(
                    connection = null,
                    developerConnection = null,
                    url = "https://github.com/ajzbc/kanye.rest?tab=MIT-1-ov-file"
                ),
                licenses = persistentSetOf(
                    License(
                        name = "MIT license",
                        url = "https://opensource.org/license/mit",
                        year = "2022",
                        hash = ""
                    )
                )
            ),
            Library(
                uniqueId = "",
                artifactVersion = "0.3",
                name = "quotable",
                description = "Quotable is a free, open source quotations API.",
                website = "https://github.com/lukePeavey/quotable",
                developers = persistentListOf(Developer(name = "Luke Peavey", organisationUrl = null)),
                organization = null,
                scm = Scm(
                    connection = null,
                    developerConnection = null,
                    url = "https://github.com/lukePeavey/quotable?tab=MIT-1-ov-file"
                ),
                licenses = persistentSetOf(
                    License(
                        name = "MIT license",
                        url = "https://opensource.org/license/mit",
                        year = "2019",
                        hash = ""
                    )
                )
            )
        )

        licenses.addAll(customLibraries)
        licenses.addAll(libs.libraries)

        LazyColumn(Modifier.padding(innerPadding)) {
            itemsIndexed(licenses) { _, item ->
                LibraryItemCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    library = item
                )
            }
        }
    }
}