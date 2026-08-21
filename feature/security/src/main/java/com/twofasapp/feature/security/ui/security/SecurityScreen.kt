/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.security.ui.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
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
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinTimeout
import com.twofasapp.data.session.domain.PinTrials
import com.twofasapp.feature.security.biometric.BiometricKeyProvider
import com.twofasapp.feature.security.ui.biometric.BiometricDialog
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.twofasapp.locale.R as LocaleR

@Composable
internal fun SecurityScreen(
    viewModel: SecurityViewModel = koinViewModel(),
    biometricKeyProvider: BiometricKeyProvider = koinInject(),
    navigator: Navigator = koinInject(),
    openSetupPin: () -> Unit = { navigator.open(Screen.SetupPin) },
    openDisablePin: () -> Unit = { navigator.open(Screen.DisablePin) },
    openChangePin: () -> Unit = { navigator.open(Screen.ChangePin) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        biometricKeyProvider = biometricKeyProvider,
        onSetupPin = openSetupPin,
        onDisablePin = openDisablePin,
        onChangePin = openChangePin,
        onPinTrialsChange = { viewModel.updatePinTrails(it) },
        onPinTimeoutChange = { viewModel.updatePinTimeout(it) },
        onBiometricLockChange = { viewModel.updateBiometricLock(it) },
        onToggleScreenshots = { viewModel.toggleScreenshots() },
    )
}

@Composable
private fun Content(
    uiState: SecurityUiState,
    biometricKeyProvider: BiometricKeyProvider? = null,
    onSetupPin: () -> Unit = {},
    onDisablePin: () -> Unit = {},
    onChangePin: () -> Unit = {},
    onPinTrialsChange: (PinTrials) -> Unit = {},
    onPinTimeoutChange: (PinTimeout) -> Unit = {},
    onBiometricLockChange: (Boolean) -> Unit = {},
    onToggleScreenshots: () -> Unit = {},
) {
    val strings = MdtLocale.strings

    var showTrailsDialog by remember { mutableStateOf(false) }
    var showTimeoutDialog by remember { mutableStateOf(false) }
    var showBiometricDialog by remember { mutableStateOf(false) }
    var showScreenshotsConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = strings.settingsSecurity) },
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .padding(padding),
        ) {
            OptionHeader(
                text = "App lock",
                contentPadding = OptionHeaderContentPaddingFirst,
            )

            OptionSwitch(
                title = stringResource(id = LocaleR.string.settings__pin_code),
                icon = MdtIcons.PinCode,
                checked = uiState.lockMethod != LockMethod.NoLock,
                onToggle = { isChecked -> if (isChecked) onSetupPin() else onDisablePin() },
            )

            if (uiState.lockMethod != LockMethod.NoLock) {
                OptionEntry(
                    title = stringResource(id = LocaleR.string.security__change_pin),
                    icon = MdtIcons.Change,
                    onClick = { onChangePin() },
                )
            }

            OptionSwitch(
                title = stringResource(id = LocaleR.string.settings__option_fingerprint),
                subtitle = if (uiState.lockMethod == LockMethod.NoLock) stringResource(id = LocaleR.string.settings__option_fingerprint_description) else null,
                icon = MdtIcons.Fingerprint,
                checked = uiState.lockMethod == LockMethod.Biometrics,
                enabled = uiState.lockMethod != LockMethod.NoLock,
                onToggle = { isChecked -> if (isChecked) showBiometricDialog = true else onBiometricLockChange(false) },
            )

            if (uiState.lockMethod != LockMethod.NoLock) {
                OptionHeader(
                    text = stringResource(id = LocaleR.string.settings__app_blocking),
                )

                OptionEntry(
                    title = stringResource(id = LocaleR.string.settings__limit_of_trials),
                    subtitle = if (uiState.pinTrials == PinTrials.NoLimit) {
                        stringResource(id = LocaleR.string.settings__no_limit)
                    } else {
                        uiState.pinTrials.label
                    },
                    icon = MdtIcons.Stop,
                    onClick = { showTrailsDialog = true },
                )

                OptionEntry(
                    title = stringResource(id = LocaleR.string.settings__block_for),
                    subtitle = stringResource(id = uiState.pinTimeout.label),
                    icon = MdtIcons.Time,
                    enabled = uiState.pinTrials != PinTrials.NoLimit,
                    onClick = { showTimeoutDialog = true },
                )
            }

            OptionHeader(
                text = "Other",
            )

            OptionSwitch(
                title = stringResource(id = LocaleR.string.settings__option_screenshots),
                subtitle = stringResource(id = LocaleR.string.settings__option_screenshots_description),
                icon = MdtIcons.Screenshot,
                checked = uiState.allowScreenshots,
                onToggle = { isChecked ->
                    if (isChecked) {
                        showScreenshotsConfirmDialog = true
                    } else {
                        onToggleScreenshots()
                    }
                },
            )
        }
    }

    if (showTrailsDialog) {
        ListRadioDialog(
            title = stringResource(id = LocaleR.string.settings__limit_of_trials),
            body = stringResource(id = LocaleR.string.settings__how_many_attempts_footer),
            icon = MdtIcons.Stop,
            options = PinTrials.entries.map {
                if (it == PinTrials.NoLimit) {
                    stringResource(id = LocaleR.string.settings__no_limit)
                } else {
                    it.label
                }
            },
            selectedOption = if (uiState.pinTrials == PinTrials.NoLimit) {
                stringResource(id = LocaleR.string.settings__no_limit)
            } else {
                uiState.pinTrials.label
            },
            onDismissRequest = { showTrailsDialog = false },
            onOptionSelected = { index, _ -> onPinTrialsChange(PinTrials.entries[index]) },
        )
    }

    if (showTimeoutDialog) {
        ListRadioDialog(
            title = stringResource(id = LocaleR.string.settings__block_for),
            body = stringResource(id = LocaleR.string.settings__block_for_footer),
            icon = MdtIcons.Time,
            options = PinTimeout.entries.map { stringResource(id = it.label) },
            selectedOption = stringResource(id = uiState.pinTimeout.label),
            onDismissRequest = { showTimeoutDialog = false },
            onOptionSelected = { index, _ -> onPinTimeoutChange(PinTimeout.entries[index]) },
        )
    }

    if (showBiometricDialog && biometricKeyProvider != null) {
        BiometricDialog(
            title = stringResource(id = LocaleR.string.biometric_dialog_setup_title),
            subtitle = stringResource(id = LocaleR.string.biometric_dialog_auth_subtitle),
            negative = stringResource(id = LocaleR.string.biometric_dialog_setup_cancel),
            onSuccess = {
                onBiometricLockChange(true)
                showBiometricDialog = false
            },
            onDismiss = {
                showBiometricDialog = false
            },
            requireKeyValidation = false,
            biometricKeyProvider = biometricKeyProvider,
        )
    }

    if (showScreenshotsConfirmDialog) {
        ConfirmDialog(
            onDismissRequest = { showScreenshotsConfirmDialog = false },
            title = stringResource(id = LocaleR.string.settings__option_screenshots_confirm_title),
            body = stringResource(id = LocaleR.string.settings__option_screenshots_confirm_description),
            icon = MdtIcons.Screenshot,
            negative = stringResource(id = LocaleR.string.commons__no),
            positive = stringResource(id = LocaleR.string.commons__yes),
            onPositive = { onToggleScreenshots() },
        )
    }
}

@Preview
@Composable
private fun PreviewNoLock() {
    PreviewTheme {
        Content(
            uiState = SecurityUiState(
                lockMethod = LockMethod.NoLock,
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewLock() {
    PreviewTheme {
        Content(
            uiState = SecurityUiState(
                lockMethod = LockMethod.Biometrics,
            ),
        )
    }
}