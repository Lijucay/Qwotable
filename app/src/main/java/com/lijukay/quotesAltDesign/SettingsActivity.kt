package com.lijukay.quotesAltDesign

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lijukay.quotesAltDesign.ui.composables.TopAppBar
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QwotableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsPreferenceList()
                }
            }
        }
    }

    @Preview
    @Composable
    fun SettingsPreferenceList(modifier: Modifier = Modifier) {
        val context = LocalContext.current

        Scaffold(
            modifier = modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = "Settings",
                    showSettingsIcon = false
                )
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .padding(paddingValues)
            ) {
                Card(
                    modifier = modifier
                        .padding(
                            start = 16.dp,
                            top = 8.dp,
                            end = 16.dp,
                            bottom = 2.dp
                        )
                        .fillMaxWidth()
                        .clickable { context.startActivity(Intent(context, LicensesActivity::class.java)) },
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )
                ) {
                    Text(
                        text = "Licenses",
                        modifier = modifier
                            .padding(16.dp)
                    )
                }
                Card(
                    modifier = modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 2.dp
                        )
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Website",
                        modifier = modifier
                            .padding(16.dp)
                    )
                }
                Card(
                    modifier = modifier
                        .padding(
                            top = 2.dp,
                            bottom = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 4.dp,
                        bottomStart = 24.dp,
                        bottomEnd = 24.dp
                    )
                ) {
                    Text(
                        text = "GitHub",
                        modifier = modifier
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}