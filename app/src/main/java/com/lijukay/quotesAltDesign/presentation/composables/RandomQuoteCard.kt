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

package com.lijukay.quotesAltDesign.presentation.composables

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.remote.model.RemoteQwotable
import com.lijukay.quotesAltDesign.data.shared.Qwotable
import com.lijukay.quotesAltDesign.domain.util.ClipboardUtil.copyToClipboard
import com.lijukay.quotesAltDesign.domain.util.PreferenceKey
import com.lijukay.quotesAltDesign.domain.util.ShareUtil.share
import com.lijukay.quotesAltDesign.domain.util.dataStore
import com.lijukay.quotesAltDesign.domain.util.states.SharePreferences
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel
import kotlinx.coroutines.flow.map
import androidx.core.net.toUri

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RandomQuoteCard(
    modifier: Modifier = Modifier,
    randomQuote: Qwotable,
    showCard: Boolean,
    uiViewModel: UIViewModel,
    onLoadNewRandomQuote: () -> Unit,
    onShowInfoClicked: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by uiViewModel.isRandomQuoteLoading.collectAsState()
    val sharePreference by context.dataStore.data.map { preferences ->
        preferences[PreferenceKey.SHARE_PREFERENCE_KEY] ?: SharePreferences.EVERYTHING.ordinal
    }.collectAsState(SharePreferences.EVERYTHING.ordinal)
    
    AnimatedVisibility(
        visible = showCard,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(34.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Text(
                text = randomQuote.quote,
                modifier = Modifier
                    .animateContentSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 4.dp
                    ),
                fontWeight = FontWeight.Bold
            )

            AnimatedVisibility(
                visible = randomQuote.hasAuthor,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    ),
                    text = randomQuote.author
                )
            }

            AnimatedVisibility(
                visible = randomQuote.hasSource,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    modifier = Modifier.padding(
                        top = 4.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                        start = 16.dp
                    ),
                    text = randomQuote.source
                )
            }

            AnimatedVisibility(
                visible = (randomQuote is RemoteQwotable)
            ) {
                if (randomQuote is RemoteQwotable) {
                    Text(
                        modifier = Modifier.padding(
                            top = 4.dp,
                            end = 16.dp,
                            bottom = 16.dp,
                            start = 16.dp
                        ),
                        text = randomQuote.apiName
                    )
                }
            }

            FlowRow(
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 8.dp
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    modifier = Modifier.padding(bottom = 4.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    onClick = onLoadNewRandomQuote,
                    enabled = !isLoading
                ) {
                    AnimatedVisibility(
                        visible = isLoading,
                        enter = expandHorizontally(),
                        exit = shrinkVertically()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            strokeWidth = 3.dp,
                            strokeCap = StrokeCap.Round
                        )
                    }

                    AnimatedVisibility(
                        visible = !isLoading,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.tertiaryContainer,
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null
                        )
                    }
                }

                Row {
                    AnimatedVisibility(
                        visible = randomQuote is RemoteQwotable
                    ) {
                        IconButton(
                            onClick = {
                                val uri: String? = when (randomQuote) {
                                    is RemoteQwotable -> randomQuote.apiSource
                                    else -> null
                                }

                                uri?.let {
                                    context.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            it.toUri()
                                        )
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                                contentDescription = null
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            copyToClipboard(context, randomQuote.quote)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = stringResource(id = android.R.string.copy)
                        )
                    }

                    IconButton(
                        onClick = {
                            randomQuote.share(context, sharePreference)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(id = R.string.share)
                        )
                    }

                    IconButton(
                        onClick = onShowInfoClicked
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                            contentDescription = stringResource(id = R.string.info)
                        )
                    }
                }
            }
        }
    }
}