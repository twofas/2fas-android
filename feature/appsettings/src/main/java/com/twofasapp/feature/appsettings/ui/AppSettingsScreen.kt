package com.twofasapp.feature.appsettings.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.common.TwTopAppBar
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AppSettingsRoute(
    viewModel: AppSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppSettingsScreen(
        uiState = uiState,
        onShowNextTokenClick = { viewModel.toggleShowNextToken() },
        onAppThemeSelect = { viewModel.setAppTheme() }
    )
}

@Composable
private fun AppSettingsScreen(
    uiState: AppSettingsUiState,
    onShowNextTokenClick: () -> Unit,
    onAppThemeSelect: () -> Unit,
) {
    Scaffold(
        topBar = { TwTopAppBar(titleText = "App Settings") }
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {

        }
    }
}

//item {
//    SimpleEntry(
//        title = stringResource(id = R.string.settings__option_theme),
//        subtitle = when (uiState.theme) {
//            AppTheme.AUTO -> stringResource(R.string.settings__theme_option_auto)
//            AppTheme.LIGHT -> stringResource(R.string.settings__theme_option_light)
//            AppTheme.DARK -> stringResource(R.string.settings__theme_option_dark)
//        },
//        subtitleGravity = SubtitleGravity.END,
//        icon = painterResource(id = R.drawable.ic_option_theme),
//        click = { router.navigate(SettingsDirections.Theme) }
//    )
//}
//
//item {
//    SwitchEntry(
//        title = stringResource(id = R.string.settings__show_next_token),
//        icon = painterResource(id = R.drawable.ic_next_token),
//        isChecked = uiState.showNextToken,
//        switch = { isChecked -> viewModel.changeShowNextToken(isChecked) }
//    )
//}
