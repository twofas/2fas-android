package com.twofasapp.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.feature.home.navigation.HomeGraph
import com.twofasapp.feature.startup.navigation.StartupGraph
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val navController: NavHostController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = TwTheme.color.background,
        ) {
            uiState?.let {
                val startDestination = when (it) {
                    MainUiState.ShowOnboarding -> StartupGraph.route
                    MainUiState.ShowHome -> HomeGraph.route
                }

                MainNavHost(navController, startDestination = startDestination)
            }
        }
    }
}