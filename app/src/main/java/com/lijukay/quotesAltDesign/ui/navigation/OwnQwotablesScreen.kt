package com.lijukay.quotesAltDesign.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.ui.composables.OwnQwotableList
import com.lijukay.quotesAltDesign.ui.dialogs.AddEditDialog

@Composable
fun OwnQwotablesScreen(modifier: Modifier = Modifier, qwotableViewModel: QwotableViewModel) {
    var showAddEditDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddEditDialog = true }
            ) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
                Text(
                    text = stringResource(id = R.string.edit_qwotable),
                    modifier = modifier.padding(start = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        OwnQwotableList(
            qwotableViewModel = qwotableViewModel,
            modifier = modifier.padding(paddingValues)
        )
    }

    if (showAddEditDialog) {
        AddEditDialog(
            onDismissRequest = { showAddEditDialog = false }
        ) {
            showAddEditDialog = false
        }
    }
}