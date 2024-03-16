package com.lijukay.quotesAltDesign.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lijukay.core.database.Qwotable

@Composable
fun QwotableItemCard(modifier: Modifier = Modifier, qwotable: Qwotable, onClick: (() -> Unit)?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .padding(all = 8.dp)
            .fillMaxWidth(),
        onClick = {
            onClick?.let { callback -> callback() }
        }
    ) {
        Text(
            text = qwotable.qwotable,
            modifier = modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp
                )
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = qwotable.author,
            modifier = modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp
                )
                .fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = qwotable.source,
            modifier = modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
