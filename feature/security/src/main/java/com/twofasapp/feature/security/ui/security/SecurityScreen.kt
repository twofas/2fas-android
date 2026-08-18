package com.twofasapp.feature.security.ui.security

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.SettingsDivider
import com.twofasapp.core.design.feature.settings.SettingsHeader
import com.twofasapp.core.design.feature.settings.SettingsLink
import com.twofasapp.core.design.feature.settings.SettingsSwitch
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.dialog.ListRadioDialog
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinTimeout
import com.twofasapp.data.session.domain.PinTrials
import com.twofasapp.feature.security.biometric.BiometricKeyProvider
import com.twofasapp.feature.security.ui.biometric.BiometricDialog
import com.twofasapp.locale.R
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun SecurityScreen(
    viewModel: SecurityViewModel = koinViewModel(),
    biometricKeyProvider: BiometricKeyProvider = koinInject(),
    openSetupPin: () -> Unit,
    openDisablePin: () -> Unit,
    openChangePin: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val activity = (LocalContext.current as? Activity)

    var showTrailsDialog by remember { mutableStateOf(false) }
    var showTimeoutDialog by remember { mutableStateOf(false) }
    var showBiometricDialog by remember { mutableStateOf(false) }
    var showScreenshotsConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(titleText = stringResource(id = R.string.settings__security))
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            if (uiState.lockMethod == LockMethod.NoLock) {
                item {
                    SettingsSwitch(
                        title = stringResource(id = R.string.settings__pin_code),
                        icon = MdtIcons.PinCode,
                        checked = false,
                        onCheckedChange = { openSetupPin() },
                    )
                }

                item { SettingsDivider() }
                item { SettingsHeader(title = stringResource(id = R.string.settings__biometrics)) }

                item {
                    SettingsSwitch(
                        title = stringResource(id = R.string.settings__option_fingerprint),
                        subtitle = stringResource(id = R.string.settings__option_fingerprint_description),
                        icon = MdtIcons.Fingerprint,
                        checked = false,
                        enabled = false,
                        onCheckedChange = { isChecked -> },
                    )
                }

                item { Divider(color = MdtTheme.color.divider, modifier = Modifier.padding(vertical = 8.dp)) }

                item {
                    SettingsSwitch(
                        title = stringResource(id = R.string.settings__option_screenshots),
                        subtitle = stringResource(id = R.string.settings__option_screenshots_description),
                        icon = MdtIcons.Screenshot,
                        checked = uiState.allowScreenshots,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                showScreenshotsConfirmDialog = true
                            } else {
                                viewModel.toggleScreenshots()
                            }
                        },
                    )
                }
            } else {
                item { SettingsHeader(title = stringResource(id = R.string.settings__settings)) }

                item {
                    SettingsSwitch(
                        title = stringResource(id = R.string.settings__pin_code),
                        icon = MdtIcons.PinCode,
                        checked = true,
                        onCheckedChange = { openDisablePin() },
                    )
                }

                item {
                    SettingsLink(
                        title = stringResource(id = R.string.security__change_pin),
                        icon = MdtIcons.Change,
                        onClick = { openChangePin() },
                    )
                }

                item { SettingsDivider() }
                item { SettingsHeader(title = stringResource(id = R.string.settings__app_blocking)) }

                item {
                    SettingsLink(
                        title = stringResource(id = R.string.settings__limit_of_trials),
                        icon = MdtIcons.Stop,
                        subtitleGravity = com.twofasapp.core.design.feature.settings.SubtitleGravity.End,
                        subtitle = if (uiState.pinTrials == PinTrials.NoLimit) {
                            stringResource(id = R.string.settings__no_limit)
                        } else {
                            uiState.pinTrials.label
                        },
                        onClick = { showTrailsDialog = true },
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.settings__how_many_attempts_footer),
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, color = MdtTheme.color.onSurfaceSecondary),
                    )
                }

                item {
                    SettingsLink(
                        title = stringResource(id = R.string.settings__block_for),
                        icon = MdtIcons.Time,
                        subtitleGravity = com.twofasapp.core.design.feature.settings.SubtitleGravity.End,
                        subtitle = stringResource(id = uiState.pinTimeout.label),
                        enabled = uiState.pinTrials != PinTrials.NoLimit,
                        onClick = { showTimeoutDialog = true },
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.settings__block_for_footer),
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, color = MdtTheme.color.onSurfaceSecondary),
                    )
                }

                item { SettingsDivider() }
                item { SettingsHeader(title = stringResource(id = R.string.settings__biometrics)) }

                item {
                    SettingsSwitch(
                        title = stringResource(id = R.string.settings__option_fingerprint),
                        icon = MdtIcons.Fingerprint,
                        checked = uiState.lockMethod == LockMethod.Biometrics,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                showBiometricDialog = true
                            } else {
                                viewModel.updateBiometricLock(false)
                            }
                        },
                    )
                }

                item { Divider(color = MdtTheme.color.divider, modifier = Modifier.padding(vertical = 8.dp)) }

                item {
                    SettingsSwitch(
                        title = stringResource(id = R.string.settings__option_screenshots),
                        subtitle = stringResource(id = R.string.settings__option_screenshots_description),
                        icon = MdtIcons.Screenshot,
                        checked = uiState.allowScreenshots,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                showScreenshotsConfirmDialog = true
                            } else {
                                viewModel.toggleScreenshots()
                            }
                        },
                    )
                }

                item { SettingsDivider() }
            }
        }

        if (showTrailsDialog) {
            ListRadioDialog(
                options = PinTrials.values().map {
                    if (it == PinTrials.NoLimit) {
                        stringResource(id = R.string.settings__no_limit)
                    } else {
                        it.label
                    }
                },
                selectedOption = if (uiState.pinTrials == PinTrials.NoLimit) stringResource(id = R.string.settings__no_limit) else uiState.pinTrials.label,
                onDismissRequest = { showTrailsDialog = false },
                onOptionSelected = { index, _ -> viewModel.updatePinTrails(PinTrials.values()[index]) },
            )
        }

        if (showTimeoutDialog) {
            ListRadioDialog(
                options = PinTimeout.values().map { stringResource(id = it.label) },
                selectedOption = stringResource(id = uiState.pinTimeout.label),
                onDismissRequest = { showTimeoutDialog = false },
                onOptionSelected = { index, _ -> viewModel.updatePinTimeout(PinTimeout.values()[index]) },
            )
        }

        if (showBiometricDialog) {
            BiometricDialog(
                title = stringResource(id = R.string.biometric_dialog_setup_title),
                subtitle = stringResource(id = R.string.biometric_dialog_auth_subtitle),
                negative = stringResource(id = R.string.biometric_dialog_setup_cancel),
                onSuccess = {
                    viewModel.updateBiometricLock(true)
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
                title = stringResource(id = R.string.settings__option_screenshots_confirm_title),
                body = stringResource(id = R.string.settings__option_screenshots_confirm_description),
                negative = stringResource(id = R.string.commons__no),
                positive = stringResource(id = R.string.commons__yes),
                onPositive = { viewModel.toggleScreenshots() },
            )
        }
    }
}