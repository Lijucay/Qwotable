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

package com.lijukay.quotesAltDesign.presentation.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.domain.util.states.Languages
import com.lijukay.quotesAltDesign.presentation.composables.RadioButtonCard
import com.lijukay.quotesAltDesign.presentation.viewmodels.UIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheetDialog(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    uiViewModel: UIViewModel
) {
    val context = LocalContext.current
    val currentLanguage by uiViewModel.selectedLanguage.collectAsState()

    val languageOptions = listOf(Languages.ENGLISH, Languages.GERMAN, Languages.FRENCH)
    var currentSelected by remember { mutableIntStateOf(
        if (currentLanguage != Languages.DEFAULT) { currentLanguage.ordinal } else {
            when (currentLanguage.displayName.asString(context)) {
                "English" -> Languages.ENGLISH.ordinal
                "German" -> Languages.GERMAN.ordinal
                "French" -> Languages.FRENCH.ordinal
                else -> -1
            }
        })
    }

    val selectedLanguage by remember { mutableStateOf(currentLanguage) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onDismissRequest,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
            }

            Text(
                text = stringResource(id = R.string.filter),
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = {
                    uiViewModel.setSelectedLanguageOption(selectedLanguage)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Rounded.Save,
                    contentDescription = null
                )
            }
        }

        languageOptions.forEachIndexed { index, language ->
            RadioButtonCard(
                modifier = Modifier.padding(
                    top = if (index == 0) 8.dp else 1.dp,
                    bottom = if (index == (languageOptions.size - 1)) 8.dp else 1.dp,
                    start = 8.dp,
                    end = 8.dp
                ),
                shape = RoundedCornerShape(
                    topStart = if (index == 0) 24.dp else 4.dp,
                    topEnd = if (index == 0) 24.dp else 4.dp,
                    bottomEnd = if (index == (languageOptions.size - 1)) 24.dp else 4.dp,
                    bottomStart = if (index == (languageOptions.size - 1)) 24.dp else 4.dp
                ),
                selected = currentSelected == language.ordinal,
                title = language.displayName.asString(context)
            ) { currentSelected = language.ordinal }
        }
    }
}