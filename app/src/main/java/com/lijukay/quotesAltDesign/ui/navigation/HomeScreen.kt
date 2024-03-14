package com.lijukay.quotesAltDesign.ui.navigation

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lijukay.core.utils.RandomQuote
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var randomQuote by remember { mutableStateOf("") }
    val context = LocalContext.current
    var loading by remember {
        mutableStateOf(true)
    }
    loadRandomQuote(context) {
        loading = false
        randomQuote = it
    }

    QwotableTheme {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Card(
                    modifier = modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = randomQuote,
                        modifier = modifier
                            .padding(16.dp)
                    )
                    Button(
                        onClick = {
                            loading = true
                            loadRandomQuote(context) {
                                loading = false
                                randomQuote = it
                            }
                        },
                        modifier = modifier
                            .padding(
                                start = 16.dp,
                                bottom = 16.dp
                            )
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = modifier
                                    .size(16.dp),
                                color = MaterialTheme.colorScheme.onSecondary,
                                strokeWidth = 3.dp,
                                strokeCap = StrokeCap.Round
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.refresh),
                            modifier = modifier
                                .padding(
                                    start =
                                    if (loading) {
                                        8.dp
                                    } else {
                                        0.dp
                                    }
                                )
                        )
                    }
                }

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(id = R.string.about_random_quotes),
                        modifier = modifier
                            .padding(
                                horizontal = 8.dp
                            )
                    )
                }
            }
        }
    }
}

private fun loadRandomQuote(context: Context, result: (String) -> Unit) {
    val randomNum = (0..1).random()
    RandomQuote(context).getRandomQuote(randomNum) {
        result(it)
    }
}
