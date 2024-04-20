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

package com.lijukay.quotesAltDesign.ui.composables.widgets

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.lijukay.core.R
import com.lijukay.quotesAltDesign.data.UIViewModel
import com.lijukay.quotesAltDesign.ui.navigation.activities.SettingsActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    showSettingsIcon: Boolean,
    showFilterIcon: Boolean,
    showBackIcon: Boolean = true,
    uiViewModel: UIViewModel,
    callback: (() -> Unit)?
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            if (showFilterIcon) {
                IconButton(onClick = { uiViewModel.setShowFilterBottomSheet(true) }) {
                    Icon(imageVector = Icons.Rounded.FilterAlt, contentDescription = null)
                }
            }
            if (showSettingsIcon) {
                IconButton(onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            SettingsActivity::class.java
                        )
                    )
                }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }
        },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = {
                    callback?.let { it() }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    )
}