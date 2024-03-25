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

package com.lijukay.quotesAltDesign.ui.navigation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme

@Composable
fun HomeScreen(modifier: Modifier = Modifier, uiViewModel: UIViewModel) {
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    val randomQuote = remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        uiViewModel.getRandomQuote(context)
    }

    DisposableEffect(key1 = uiViewModel) {
        val loadingObserver = Observer<Boolean> { isLoadingQuote ->
            isLoading.value = isLoadingQuote
        }
        val randomQuoteObserver = Observer<String> { quote ->
            randomQuote.value = quote
        }

        uiViewModel.isLoadingRandomQuote.observeForever(loadingObserver)
        uiViewModel.randomQuote.observeForever(randomQuoteObserver)

        onDispose {
            uiViewModel.isLoadingRandomQuote.removeObserver(loadingObserver)
            uiViewModel.randomQuote.removeObserver(randomQuoteObserver)
        }
    }

    QwotableTheme {
        Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                Card(
                    modifier = modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = randomQuote.value,
                        modifier = modifier.padding(all = 16.dp)
                    )
                    Button(
                        onClick = {
                            uiViewModel.getRandomQuote(context)
                        },
                        modifier = modifier.padding(start = 16.dp, bottom = 16.dp)
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                modifier = modifier.size(size = 16.dp),
                                color = MaterialTheme.colorScheme.onSecondary,
                                strokeWidth = 3.dp,
                                strokeCap = StrokeCap.Round
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.refresh),
                            modifier = modifier.padding(start = if (isLoading.value) 8.dp else 0.dp)
                        )
                    }
                }

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.Info, contentDescription = null)
                    Text(
                        text = stringResource(id = R.string.about_random_quotes),
                        modifier = modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
