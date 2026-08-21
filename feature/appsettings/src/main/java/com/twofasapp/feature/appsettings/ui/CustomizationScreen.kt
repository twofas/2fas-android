/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.appsettings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPaddingFirst
import com.twofasapp.core.design.feature.settings.OptionSwitch
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.dialog.ListRadioDialog
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.theme.RoundedShape16
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.feature.appsettings.R
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import com.twofasapp.locale.R as LocaleR

@Composable
internal fun CustomizationScreen(
    viewModel: CustomizationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
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
private fun Content(
    uiState: CustomizationUiState,
    onSelectedThemeChange: (SelectedTheme) -> Unit = {},
    onServicesStyleChange: (ServicesStyle) -> Unit = {},
    onShowNextTokenToggle: () -> Unit = {},
    onShowBackupNoticeToggle: () -> Unit = {},
    onAutoFocusSearchToggle: () -> Unit = {},
    onHideCodesToggle: () -> Unit = {},
    onDynamicColorsToggle: () -> Unit = {},
) {
    val strings = MdtLocale.strings
    var showServicesStyleDialog by remember { mutableStateOf(false) }
    var showConfirmDisableBackupNotice by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = strings.settingsAppearance) },
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .padding(padding),
        ) {
            OptionHeader(
                text = strings.settingsTheme,
                contentPadding = OptionHeaderContentPaddingFirst,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedShape16)
                    .background(MdtTheme.color.surfaceContainerLow)
                    .padding(vertical = 24.dp),
            ) {
                SelectedTheme.entries.forEach { theme ->
                    ThemeOption(
                        modifier = Modifier.weight(1f),
                        theme = theme,
                        selected = uiState.selectedTheme == theme,
                        onClick = { onSelectedThemeChange(theme) },
                    )
                }
            }

            OptionSwitch(
                title = strings.settingsDynamicColors,
                subtitle = strings.settingsDynamicColorsBody,
                icon = MdtIcons.Theme,
                checked = uiState.dynamicColors,
                onToggle = { onDynamicColorsToggle() },
            )

            OptionHeader(
                text = strings.settingsPreferences,
            )

            OptionEntry(
                title = strings.settingsServicesStyle,
                subtitle = uiState.servicesStyle.toStringResource(),
                icon = MdtIcons.ListStyle,
                onClick = { showServicesStyleDialog = true },
            )

            OptionSwitch(
                title = strings.settingsShowNextCode,
                subtitle = strings.settingsShowNextCodeBody,
                icon = MdtIcons.NextToken,
                checked = uiState.showNextCode,
                onToggle = { onShowNextTokenToggle() },
            )

            OptionSwitch(
                title = strings.settingsHideCodes,
                subtitle = strings.settingsHideCodesBody,
                icon = MdtIcons.Eye,
                checked = uiState.hideCodes,
                onToggle = { onHideCodesToggle() },
            )

            OptionSwitch(
                title = strings.settingsAutoFocusSearch,
                subtitle = strings.settingsAutoFocusSearchBody,
                icon = MdtIcons.Search,
                checked = uiState.autoFocusSearch,
                onToggle = { onAutoFocusSearchToggle() },
            )

            OptionSwitch(
                title = strings.settingsShowBackupNotice,
                icon = MdtIcons.CloudOff,
                checked = uiState.showBackupNotice,
                onToggle = { checked ->
                    if (checked.not()) {
                        showConfirmDisableBackupNotice = true
                    } else {
                        onShowBackupNoticeToggle()
                    }
                },
            )
        }
    }

    if (showServicesStyleDialog) {
        ListRadioDialog(
            onDismissRequest = { showServicesStyleDialog = false },
            title = strings.settingsServicesStyle,
            options = ServicesStyle.entries.map { it.toStringResource() },
            selectedIndex = ServicesStyle.entries.indexOf(uiState.servicesStyle),
            onOptionSelected = { index, _ -> onServicesStyleChange(ServicesStyle.entries[index]) },
        )
    }

    if (showConfirmDisableBackupNotice) {
        ConfirmDialog(
            onDismissRequest = { showConfirmDisableBackupNotice = false },
            title = strings.settingsShowBackupNotice,
            body = strings.settingsShowBackupNoticeConfirmBody,
            icon = MdtIcons.Info,
            onPositive = { onShowBackupNoticeToggle() },
        )
    }
}

@Composable
private fun ThemeOption(
    theme: SelectedTheme,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = when (theme) {
                SelectedTheme.Auto -> painterResource(R.drawable.img_theme_auto)
                SelectedTheme.Light -> painterResource(R.drawable.img_theme_light)
                SelectedTheme.Dark -> painterResource(R.drawable.img_theme_dark)
            },
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, if (selected) MdtTheme.color.primary else MdtTheme.color.transparent, RoundedCornerShape(14.dp))
                .testTag("themeOption${theme.name}")
                .clickable { onClick() }
                .padding(4.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = theme.toStringResource(),
            style = MdtTheme.typo.material.titleMedium,
        )
    }
}

@Composable
private fun SelectedTheme.toStringResource(): String {
    return when (this) {
        SelectedTheme.Auto -> stringResource(id = LocaleR.string.settings__theme_option_auto)
        SelectedTheme.Light -> stringResource(id = LocaleR.string.settings__theme_option_light)
        SelectedTheme.Dark -> stringResource(id = LocaleR.string.settings__theme_option_dark)
    }
}

@Composable
private fun ServicesStyle.toStringResource(): String {
    return when (this) {
        ServicesStyle.Default -> stringResource(id = LocaleR.string.settings__list_style_option_default)
        ServicesStyle.Compact -> stringResource(id = LocaleR.string.settings__list_style_option_compact)
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            uiState = CustomizationUiState(),
        )
    }
}