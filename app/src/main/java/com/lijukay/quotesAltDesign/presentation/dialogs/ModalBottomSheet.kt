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

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * An androidx.compose.material3.ModalBottomSheet with a transparent navigation bar and no drag handle
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    content: @Composable (ColumnScope.() -> Unit),
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = containerColor,
    ) {
        val systemUiController = rememberSystemUiController()

        systemUiController.setNavigationBarColor(Color.Transparent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            systemUiController.isNavigationBarContrastEnforced = false

        Column(
            content = content
        )
    }
}