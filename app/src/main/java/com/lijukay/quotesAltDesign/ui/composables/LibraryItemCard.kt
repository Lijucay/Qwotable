package com.lijukay.quotesAltDesign.ui.composables

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryItemCard(library: Library, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(library.website)))
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = modifier
                .padding(16.dp)
        ) {
            Text(
                text = library.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            if (!library.description.isNullOrBlank()) {
                Text(
                    text = library.description!!,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            FlowRow(
                modifier = modifier
                    .padding(
                        top = 4.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!library.artifactVersion.isNullOrBlank()) {
                    ExtraInfoTexts(text = "Version: ${library.artifactVersion}")
                }
                library.developers.forEach { developer: Developer ->
                    if (!developer.name.isNullOrBlank()) {
                        ExtraInfoTexts(text = developer.name!!)
                    }
                }
                library.licenses.forEach { license: License ->
                    ExtraInfoTexts(text = license.name)
                }
                if (library.openSource) {
                    OpenSourceInfo()
                }
            }
        }
    }
}

@Composable
fun ExtraInfoTexts(modifier: Modifier = Modifier, text: String) {
    Card(
        shape = RoundedCornerShape(100),
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            modifier = modifier
                .padding(
                    vertical = 6.dp,
                    horizontal = 10.dp
                )
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun OpenSourceInfo(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(100),
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = "Open Source",
            modifier = modifier
                .padding(
                    vertical = 6.dp,
                    horizontal = 10.dp
                )
        )
    }
}
