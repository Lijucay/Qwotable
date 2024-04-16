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

package com.lijukay.quotesAltDesign.ui.navigation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.lijukay.core.R
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.ui.composables.item_cards.LibraryItemCard
import com.lijukay.quotesAltDesign.ui.composables.widgets.TopAppBar
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.entity.Scm
import com.mikepenz.aboutlibraries.util.withContext

class LicensesActivity : ComponentActivity() {
    private val uiViewModel: UIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QwotableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LicensesScreen()
                }
            }
        }
    }

    @Composable
    fun LicensesScreen(modifier: Modifier = Modifier) {
        val libs = Libs
            .Builder()
            .withContext(ctx = LocalContext.current)
            .build()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = stringResource(id = R.string.licenses),
                    showSettingsIcon = false,
                    showFilterIcon = false,
                    uiViewModel = uiViewModel
                ) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        ) { paddingValues ->
            val licenses: MutableList<Library> = mutableListOf()

            val customLibraries: List<Library> = listOf(
                Library(
                    uniqueId = "",
                    artifactVersion = null,
                    name = "kanye.rest API",
                    description = "A free REST API for random Kanye West quotes (Kanye as a Service).\nBuilt with Cloudflare Workers.",
                    website = "https://github.com/ajzbc/kanye.rest",
                    developers = listOf(Developer(name = "Andrew Jazbec", organisationUrl = null)),
                    organization = null,
                    scm = Scm(
                        connection = null,
                        developerConnection = null,
                        url = "https://github.com/ajzbc/kanye.rest?tab=MIT-1-ov-file"
                    ),
                    licenses = setOf(
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
                    developers = listOf(Developer(name = "Luke Peavey", organisationUrl = null)),
                    organization = null,
                    scm = Scm(
                        connection = null,
                        developerConnection = null,
                        url = "https://github.com/lukePeavey/quotable?tab=MIT-1-ov-file"
                    ),
                    licenses = setOf(
                        License(
                            name = "MIT license",
                            url = "https://opensource.org/license/mit",
                            year = "2019",
                            hash = ""
                        )
                    )
                )
            )

            licenses.addAll(elements = customLibraries)
            licenses.addAll(elements = libs.libraries)
            LazyColumn(
                modifier = modifier.padding(paddingValues = paddingValues)
            ) {
                itemsIndexed(licenses) { _, item ->
                    LibraryItemCard(library = item)
                }
            }
        }
    }
}