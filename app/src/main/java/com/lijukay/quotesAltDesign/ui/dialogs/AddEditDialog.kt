package com.lijukay.quotesAltDesign.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lijukay.core.database.Qwotable
import com.lijukay.quotesAltDesign.R

@Composable
fun AddEditDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    item: Qwotable? = null,
    onConfirmationRequest: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = {  }
    ) {
        ElevatedCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    vertical = 38.dp,
                    horizontal = 5.dp
                ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = if (item != null) {
                    stringResource(id = R.string.edit_qwotable)
                } else {
                    stringResource(id = R.string.create_qwotable)
                },
                modifier = modifier
                    .padding(16.dp)
            )
            Column(
                modifier = modifier
                    .verticalScroll(state = scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            }
        }
    }
}