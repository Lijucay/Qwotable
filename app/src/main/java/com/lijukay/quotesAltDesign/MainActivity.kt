package com.lijukay.quotesAltDesign

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lijukay.core.Screens
import com.lijukay.core.utils.QwotableViewModel
import com.lijukay.core.utils.QwotableViewModelFactory
import com.lijukay.quotesAltDesign.ui.composables.BottomNavigationBar
import com.lijukay.quotesAltDesign.ui.composables.TopAppBar
import com.lijukay.quotesAltDesign.ui.screens.FavoriteScreen
import com.lijukay.quotesAltDesign.ui.screens.HomeScreen
import com.lijukay.quotesAltDesign.ui.screens.OwnQwotablesScreen
import com.lijukay.quotesAltDesign.ui.screens.QwotableScreen
import com.lijukay.quotesAltDesign.ui.theme.QwotableTheme

class MainActivity : ComponentActivity() {
    private val qwotableViewModel: QwotableViewModel by viewModels {
        QwotableViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QwotableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainLayout(qwotableViewModel)
                }
            }
        }
    }
}

@Composable
fun MainLayout(qwotableViewModel: QwotableViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.home),
                showSettingsIcon = true
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        modifier = modifier
            .fillMaxSize()
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = modifier
                .padding(paddingValues = contentPadding)
        ) {
            composable(Screens.Home.route) {
                HomeScreen()
            }
            composable(Screens.Qwotable.route) {
                QwotableScreen(qwotableViewModel = qwotableViewModel)
            }
            composable(Screens.Favorite.route) {
                FavoriteScreen(qwotableViewModel = qwotableViewModel)
            }
            composable(Screens.OwnQwotables.route) {
                OwnQwotablesScreen(qwotableViewModel = qwotableViewModel)
            }
        }
    }
}