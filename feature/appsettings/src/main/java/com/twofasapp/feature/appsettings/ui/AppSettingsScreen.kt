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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.ConfirmDialog
import com.twofasapp.designsystem.dialog.ListRadioDialog
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.designsystem.settings.SettingsSwitch
import com.twofasapp.locale.R
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AppSettingsScreen(
    viewModel: AppSettingsViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContent(
        uiState = uiState,
        onConsumeEvent = { viewModel.consumeEvent(it) },
        onSelectedThemeChange = { viewModel.setSelectedTheme(it) },
        onServicesStyleChange = { viewModel.setServiceStyle(it) },
        onShowNextTokenToggle = { viewModel.toggleShowNextToken() },
        onShowBackupNoticeToggle = { viewModel.toggleShowBackupNotice() },
        onAutoFocusSearchToggle = { viewModel.toggleAutoFocusSearch() },
        onHideCodesToggle = { viewModel.toggleHideTokens() }
    )
}

@Composable
private fun ScreenContent(
    uiState: AppSettingsUiState,
    onConsumeEvent: (AppSettingsUiEvent) -> Unit,
    onSelectedThemeChange: (SelectedTheme) -> Unit,
    onServicesStyleChange: (ServicesStyle) -> Unit,
    onShowNextTokenToggle: () -> Unit,
    onShowBackupNoticeToggle: () -> Unit,
    onAutoFocusSearchToggle: () -> Unit,
    onHideCodesToggle: () -> Unit,
) {
    val activity = LocalContext.currentActivity
    var showThemeDialog by remember { mutableStateOf(false) }
    var showServicesStyleDialog by remember { mutableStateOf(false) }
    var showConfirmDisableBackupNotice by remember { mutableStateOf(false) }

    uiState.events.firstOrNull()?.let {
        onConsumeEvent(it)

        when (it) {
            AppSettingsUiEvent.Recreate -> activity.recreate()
        }
    }

    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.settingsAppearance) }
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            item {
                SettingsLink(
                    title = TwLocale.strings.settingsTheme,
                    subtitle = uiState.appSettings.selectedTheme.toStringResource(),
                    icon = TwIcons.Theme,
                    onClick = { showThemeDialog = true }
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.settingsServicesStyle,
                    subtitle = uiState.appSettings.servicesStyle.toStringResource(),
                    icon = TwIcons.ListStyle,
                    onClick = { showServicesStyleDialog = true }
                )
            }

            item {
                SettingsSwitch(
                    title = TwLocale.strings.settingsShowNextCode,
                    subtitle = TwLocale.strings.settingsShowNextCodeBody,
                    icon = TwIcons.NextToken,
                    checked = uiState.appSettings.showNextCode,
                    onCheckedChange = { onShowNextTokenToggle() },
                )
            }

            item {
                SettingsSwitch(
                    title = TwLocale.strings.settingsAutoFocusSearch,
                    subtitle = TwLocale.strings.settingsAutoFocusSearchBody,
                    icon = TwIcons.Search,
                    checked = uiState.appSettings.autoFocusSearch,
                    onCheckedChange = { onAutoFocusSearchToggle() },
                )
            }

            item {
                SettingsSwitch(
                    title = TwLocale.strings.settingsShowBackupNotice,
                    icon = TwIcons.CloudOff,
                    checked = uiState.appSettings.showBackupNotice,
                    onCheckedChange = { checked ->
                        if (checked.not()) {
                            showConfirmDisableBackupNotice = true
                        } else {
                            onShowBackupNoticeToggle()
                        }
                    },
                )
            }

            item {
                SettingsSwitch(
                    title = TwLocale.strings.settingsHideCodes,
                    subtitle = TwLocale.strings.settingsHideCodesBody,
                    icon = TwIcons.Eye,
                    checked = uiState.appSettings.hideCodes,
                    onCheckedChange = { onHideCodesToggle() },
                )
            }
        }

        if (showThemeDialog) {
            ListRadioDialog(
                onDismissRequest = { showThemeDialog = false },
                title = TwLocale.strings.settingsTheme,
                options = SelectedTheme.values().map { it.toStringResource() },
                selectedOption = uiState.appSettings.selectedTheme.toStringResource(),
                onOptionSelected = { index, _ -> onSelectedThemeChange(SelectedTheme.values()[index]) },
            )
        }

        if (showServicesStyleDialog) {
            ListRadioDialog(
                onDismissRequest = { showServicesStyleDialog = false },
                title = TwLocale.strings.settingsServicesStyle,
                options = ServicesStyle.values().map { it.toStringResource() },
                selectedOption = uiState.appSettings.servicesStyle.toStringResource(),
                onOptionSelected = { index, _ -> onServicesStyleChange(ServicesStyle.values()[index]) },
            )
        }

        if (showConfirmDisableBackupNotice) {
            ConfirmDialog(
                onDismissRequest = { showConfirmDisableBackupNotice = false },
                title = TwLocale.strings.settingsShowBackupNotice,
                body = TwLocale.strings.settingsShowBackupNoticeConfirmBody,
                onPositive = { onShowBackupNoticeToggle() }
            )
        }
    }
}

@Composable
private fun SelectedTheme.toStringResource(): String {
    return when (this) {
        SelectedTheme.Auto -> stringResource(id = R.string.settings__theme_option_auto)
        SelectedTheme.Light -> stringResource(id = R.string.settings__theme_option_light)
        SelectedTheme.Dark -> stringResource(id = R.string.settings__theme_option_dark)
    }
}

@Composable
private fun ServicesStyle.toStringResource(): String {
    return when (this) {
        ServicesStyle.Default -> stringResource(id = R.string.settings__list_style_option_default)
        ServicesStyle.Compact -> stringResource(id = R.string.settings__list_style_option_compact)
    }
}