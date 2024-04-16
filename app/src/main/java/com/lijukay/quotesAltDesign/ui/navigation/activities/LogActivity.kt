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

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lijukay.core.R
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme

class LogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QwotableTheme {
                val intent = intent
                val modifier = Modifier
                Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LogLayout(intent = intent)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun LogLayout(modifier: Modifier = Modifier, intent: Intent? = Intent()) {
        var logText: String = stringResource(id = R.string.no_logs)
        var detailedLogText: String = stringResource(id = R.string.no_logs)
        val scrollState = rememberScrollState()

        intent?.let { results ->
            results.getStringExtra("logs")?.let { logText = it }
            results.getStringExtra("logs_detailed")?.let { detailedLogText = it }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.logs)) },
                    actions = {
                        IconButton(onClick = { /*TODO: Copy logs*/ }) {
                            Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = stringResource(id = android.R.string.copy))
                        }
                        IconButton(onClick = { /*TODO: Save logs*/ }) {
                            Icon(imageVector = Icons.Rounded.Save, contentDescription = stringResource(id = R.string.save))
                        }
                        IconButton(onClick = { /*TODO: Share logs*/ }) {
                            Icon(imageVector = Icons.Rounded.Share, contentDescription = stringResource(id = R.string.share))
                        }

                    }
                )
            },
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .padding(paddingValues = paddingValues)
                    .verticalScroll(state = scrollState)
                    .fillMaxSize()
            ) {
                Card(
                    modifier = modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Column(
                        modifier = modifier.padding(all = 16.dp)
                    ) {
                        Text(
                            modifier = modifier.padding(bottom = 8.dp),
                            text = stringResource(id = R.string.logs)
                        )
                        Text(text = logText)
                    }
                }
                Card(
                    modifier = modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Column(
                        modifier = modifier.padding(all = 16.dp)
                    ) {
                        Text(
                            modifier = modifier.padding(bottom = 8.dp),
                            text = stringResource(id = R.string.detailed_logs)
                        )
                        Text(text = detailedLogText)
                    }
                }
            }
        }
    }
}