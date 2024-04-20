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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lijukay.core.R
import com.lijukay.core.utils.ClipboardUtil.Companion.copyToClipboard
import com.lijukay.core.utils.FilesUtil.Companion.saveFile
import com.lijukay.core.utils.FilesUtil.Companion.saveFileToDownloads
import com.lijukay.core.utils.ShareUtil.Companion.shareExternally
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QwotableTheme {
                val intent = intent
                val modifier = Modifier
                Surface(
                    modifier = modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
        var filesDir: String? = null
        rememberScrollState()

        intent?.let { results ->
            results.getStringExtra("filePath")?.let { filesDir = it }
            results.getStringExtra("logs")?.let { logText = it }
            results.getStringExtra("logs_detailed")?.let { detailedLogText = it }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "") },
                    actions = {
                        IconButton(
                            onClick = {
                                detailedLogText.copyToClipboard(this@LogActivity)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = stringResource(id = android.R.string.copy)
                            )
                        }
                        IconButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    runCatching {
                                        if (filesDir != null) {
                                            val result = saveFileToDownloads(filesDir)
                                            if (result) {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(
                                                        this@LogActivity,
                                                        R.string.success,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            logText.saveFile(
                                                this@LogActivity,
                                                "logs_${System.currentTimeMillis()}"
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Save,
                                contentDescription = stringResource(id = R.string.save)
                            )
                        }
                        IconButton(
                            onClick = {
                                logText.shareExternally(this@LogActivity)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = stringResource(id = R.string.share)
                            )
                        }

                    }
                )
            },
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            // TODO: CHANGE THE LAYOUT OF THIS SCREEN TO SOMETHING MORE HUMANE LOL
            Column(
                modifier = modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bug),
                    contentDescription = null,
                    modifier = modifier
                        .height(150.dp)
                        .width(150.dp)
                )

                val linkText = stringResource(id = R.string.bug_message).toAnnotatedLinkString(
                    hyperLinks = mapOf("GitHub" to "https://github.com/Lijucay/Qwotable/issues/new"),
                    linkTextColor = MaterialTheme.colorScheme.primary
                )
                val uriHandler = LocalUriHandler.current

                ClickableText(
                    modifier = modifier.padding(16.dp),
                    text = linkText,
                    onClick = { offset ->
                        linkText
                            .getStringAnnotations("URL", offset, offset)
                            .firstOrNull()?.let { stringAnnotation ->
                                uriHandler.openUri(stringAnnotation.item)
                            }
                    },
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }
    }

    private fun String.toAnnotatedLinkString(
        hyperLinks: Map<String, String>,
        linkTextColor: Color
    ): AnnotatedString {
        return buildAnnotatedString {
            append(this@toAnnotatedLinkString)

            for ((key, value) in hyperLinks) {
                val startI = this@toAnnotatedLinkString.indexOf(key)
                val endI = startI + key.length
                addStyle(
                    style = SpanStyle(
                        color = linkTextColor,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = startI,
                    end = endI
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = value,
                    start = startI,
                    end = endI
                )
            }
        }
    }

    @Composable
    private fun getAnnotatedString(): AnnotatedString {
        val string = getString(R.string.bug_message)

        return buildAnnotatedString {
            val text = string.split(" ")
            for (word in text) {
                if (word.lowercase().trim() != "github") {
                    append("$word ")
                } else {
                    pushStringAnnotation(
                        tag = "github",
                        annotation = "https://github.com/Lijucay/Qwotable/issues/new",
                    )
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("$word ")
                    }
                    pop()
                }
            }
        }
    }
}