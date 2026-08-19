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
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.feature.settings.SettingsLink
import com.twofasapp.core.design.feature.settings.SettingsSwitch
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.dialog.ListRadioDialog
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.locale.MdtLocale
import com.twofasapp.locale.R
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AppSettingsScreen(
    viewModel: AppSettingsViewModel = koinViewModel(),
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
        onHideCodesToggle = { viewModel.toggleHideTokens() },
        onDynamicColorsToggle = { viewModel.toggleDynamicColors() },
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
    onDynamicColorsToggle: () -> Unit,
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
        topBar = { TopAppBar(title = MdtLocale.strings.settingsAppearance) },
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            item {
                SettingsLink(
                    title = MdtLocale.strings.settingsTheme,
                    subtitle = uiState.appSettings.selectedTheme.toStringResource(),
                    icon = MdtIcons.Theme,
                    onClick = { showThemeDialog = true },
                )
            }

            item {
                SettingsSwitch(
                    title = MdtLocale.strings.settingsDynamicColors,
                    subtitle = MdtLocale.strings.settingsDynamicColorsBody,
                    icon = MdtIcons.Theme,
                    checked = uiState.appSettings.dynamicColors,
                    onCheckedChange = { onDynamicColorsToggle() },
                )
            }

            item {
                SettingsLink(
                    title = MdtLocale.strings.settingsServicesStyle,
                    subtitle = uiState.appSettings.servicesStyle.toStringResource(),
                    icon = MdtIcons.ListStyle,
                    onClick = { showServicesStyleDialog = true },
                )
            }

            item {
                SettingsSwitch(
                    title = MdtLocale.strings.settingsShowNextCode,
                    subtitle = MdtLocale.strings.settingsShowNextCodeBody,
                    icon = MdtIcons.NextToken,
                    checked = uiState.appSettings.showNextCode,
                    onCheckedChange = { onShowNextTokenToggle() },
                )
            }

            item {
                SettingsSwitch(
                    title = MdtLocale.strings.settingsAutoFocusSearch,
                    subtitle = MdtLocale.strings.settingsAutoFocusSearchBody,
                    icon = MdtIcons.Search,
                    checked = uiState.appSettings.autoFocusSearch,
                    onCheckedChange = { onAutoFocusSearchToggle() },
                )
            }

            item {
                SettingsSwitch(
                    title = MdtLocale.strings.settingsShowBackupNotice,
                    icon = MdtIcons.CloudOff,
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
                    title = MdtLocale.strings.settingsHideCodes,
                    subtitle = MdtLocale.strings.settingsHideCodesBody,
                    icon = MdtIcons.Eye,
                    checked = uiState.appSettings.hideCodes,
                    onCheckedChange = { onHideCodesToggle() },
                )
            }
        }

        if (showThemeDialog) {
            ListRadioDialog(
                onDismissRequest = { showThemeDialog = false },
                title = MdtLocale.strings.settingsTheme,
                options = SelectedTheme.values().map { it.toStringResource() },
                selectedOption = uiState.appSettings.selectedTheme.toStringResource(),
                onOptionSelected = { index, _ -> onSelectedThemeChange(SelectedTheme.values()[index]) },
            )
        }

        if (showServicesStyleDialog) {
            ListRadioDialog(
                onDismissRequest = { showServicesStyleDialog = false },
                title = MdtLocale.strings.settingsServicesStyle,
                options = ServicesStyle.values().map { it.toStringResource() },
                selectedOption = uiState.appSettings.servicesStyle.toStringResource(),
                onOptionSelected = { index, _ -> onServicesStyleChange(ServicesStyle.values()[index]) },
            )
        }

        if (showConfirmDisableBackupNotice) {
            ConfirmDialog(
                onDismissRequest = { showConfirmDisableBackupNotice = false },
                title = MdtLocale.strings.settingsShowBackupNotice,
                body = MdtLocale.strings.settingsShowBackupNoticeConfirmBody,
                onPositive = { onShowBackupNoticeToggle() },
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