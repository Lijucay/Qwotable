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

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.lijukay.core.R
import com.lijukay.quotesAltDesign.data.StringValue
import com.lijukay.quotesAltDesign.data.UIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheetDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    uiViewModel: UIViewModel
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        windowInsets = WindowInsets.displayCutout.exclude(WindowInsets.safeDrawing),
        sheetState = sheetState
    ) {
        val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val context = LocalContext.current
        val currentLanguage = remember {
            mutableStateOf(StringValue.Default.asString(context)
                .ifEmpty { context.getString(R.string.default_language) }
            )
        }

        LaunchedEffect(key1 = Unit) {
            Log.e("hjk", currentLanguage.value)
            uiViewModel.selectedLanguage.value?.let {
                currentLanguage.value = it.asString(context)
            }
        }

        DisposableEffect(key1 = uiViewModel) {
            val languageObserver = Observer<StringValue> { current ->
                currentLanguage.value = current.asString(context)
            }

            uiViewModel.selectedLanguage.observeForever(languageObserver)

            onDispose {
                uiViewModel.selectedLanguage.removeObserver(languageObserver)
            }
        }

        val languageOptions = listOf(
            R.string.english,
            R.string.german,
            R.string.french
        )

        Column(
            modifier = modifier.padding(bottom = bottomPadding)
        ) {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.filter),
                    modifier = modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider()
            Column(
                modifier = modifier
                    .padding(vertical = 16.dp)
                    .selectableGroup()
            ) {
                languageOptions.forEach { language ->
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (StringValue.StringResource(language).asString(context) == currentLanguage.value),
                                role = Role.RadioButton,
                                onClick = {
                                    uiViewModel.setSelectedLanguageOption(language)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            modifier = modifier.padding(
                                start = 16.dp,
                                end = 8.dp
                            ),
                            selected = (StringValue.StringResource(language).asString(context) == currentLanguage.value),
                            onClick = null
                        )
                        Text(text = stringResource(id = language), modifier = modifier.padding(start = 8.dp, end = 16.dp))
                    }
                }
            }
        }
    }
}