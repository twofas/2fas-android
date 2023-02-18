package com.twofasapp.feature.appsettings.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.ListRadioDialog
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.designsystem.settings.SettingsSwitch
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AppSettingsRoute(
    viewModel: AppSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppSettingsScreen(
        uiState = uiState,
        onSelectedThemeChange = { viewModel.setSelectedTheme(it) },
        onShowNextTokenToggle = { viewModel.toggleShowNextToken() }
    )
}

@Composable
private fun AppSettingsScreen(
    uiState: AppSettingsUiState,
    onSelectedThemeChange: (SelectedTheme) -> Unit,
    onShowNextTokenToggle: () -> Unit,
) {
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.settingsAppearance) }
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            item {
                SettingsLink(
                    title = TwLocale.strings.settingsTheme,
                    subtitle = uiState.selectedTheme.name,
                    icon = TwIcons.Theme,
                    onClick = { showThemeDialog = true }
                )
            }

            item {
                SettingsSwitch(
                    title = TwLocale.strings.settingsShowNextToken,
                    checked = uiState.showNextToken,
                    onCheckedChange = { onShowNextTokenToggle() },
                    subtitle = "Show next token when current one is about to expire.",
                    icon = TwIcons.NextToken,
                )
            }
        }

        if (showThemeDialog) {
            ListRadioDialog(
                onDismissRequest = { showThemeDialog = false },
                title = TwLocale.strings.settingsTheme,
                options = SelectedTheme.values().map { it.name },
                selectedOption = uiState.selectedTheme.name,
                onOptionSelected = { index, _ -> onSelectedThemeChange(SelectedTheme.values()[index]) }
            )
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
