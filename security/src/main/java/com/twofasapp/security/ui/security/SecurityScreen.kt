package com.twofasapp.security.ui.security

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Divider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.twofasapp.design.compose.HeaderEntry
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.SubtitleGravity
import com.twofasapp.design.compose.SwitchEntry
import com.twofasapp.design.compose.dialogs.ListDialog
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.navigation.SecurityDirections
import com.twofasapp.navigation.SecurityRouter
import com.twofasapp.resources.R
import com.twofasapp.security.domain.model.LockMethod
import com.twofasapp.security.domain.model.PinTimeout
import com.twofasapp.security.domain.model.PinTrials
import com.twofasapp.security.ui.biometric.BiometricDialog
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
internal fun SecurityScreen(
    viewModel: SecurityViewModel = getViewModel(),
    router: SecurityRouter = get(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val activity = (LocalContext.current as? Activity)

    var showTrailsDialog by remember { mutableStateOf(false) }
    var showTimeoutDialog by remember { mutableStateOf(false) }
    var showBiometricDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TwTopAppBar(titleText = stringResource(id = R.string.settings__security))
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            if (uiState.lockMethod == LockMethod.NoLock) {
                item {
                    SwitchEntry(
                        title = stringResource(id = R.string.settings__pin_code),
                        icon = painterResource(id = R.drawable.ic_pin_code),
                        isChecked = false,
                        switch = { router.navigate(SecurityDirections.SetupPin) }
                    )
                }

                item { Divider(color = TwTheme.color.divider) }
                item { HeaderEntry(text = stringResource(id = R.string.settings__biometrics)) }

                item {
                    SwitchEntry(
                        title = stringResource(id = R.string.settings__option_fingerprint),
                        icon = painterResource(id = R.drawable.ic_fingerprint),
                        isChecked = false,
                        isEnabled = false,
                        switch = { isChecked -> }
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.settings__option_fingerprint_description),
                        modifier = Modifier.padding(start = 24.dp, end = 16.dp, top = 8.dp),
                        style = MaterialTheme.typography.body2.copy(color = TwTheme.color.onSurfaceSecondary)
                    )
                }

            } else {

                item { HeaderEntry(text = stringResource(id = R.string.settings__settings)) }

                item {
                    SwitchEntry(
                        title = stringResource(id = R.string.settings__pin_code),
                        icon = painterResource(id = R.drawable.ic_pin_code),
                        isChecked = true,
                        switch = { router.navigate(SecurityDirections.DisablePin) }
                    )
                }

                item {
                    SimpleEntry(
                        title = stringResource(id = R.string.security__change_pin),
                        icon = painterResource(id = R.drawable.ic_change_pin),
                        click = { router.navigate(SecurityDirections.ChangePin) }
                    )
                }

                item { Divider(color = TwTheme.color.divider) }
                item { HeaderEntry(text = stringResource(id = R.string.settings__app_blocking)) }

                item {
                    SimpleEntry(
                        title = stringResource(id = R.string.settings__limit_of_trials),
                        icon = painterResource(id = R.drawable.ic_limit_trials),
                        subtitleGravity = SubtitleGravity.END,
                        subtitle = if (uiState.pinTrials == PinTrials.NoLimit) {
                            stringResource(id = R.string.settings__no_limit)
                        } else {
                            uiState.pinTrials.label
                        },
                        click = { showTrailsDialog = true }
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.settings__how_many_attempts_footer),
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                        style = MaterialTheme.typography.body2.copy(fontSize = 14.sp, color = TwTheme.color.onSurfaceSecondary),
                    )
                }

                item {
                    SimpleEntry(
                        title = stringResource(id = R.string.settings__block_for),
                        icon = painterResource(id = R.drawable.ic_block_for),
                        subtitleGravity = SubtitleGravity.END,
                        subtitle = stringResource(id = uiState.pinTimeout.label),
                        isEnabled = uiState.pinTrials != PinTrials.NoLimit,
                        click = { showTimeoutDialog = true }
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.settings__block_for_footer),
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                        style = MaterialTheme.typography.body2.copy(fontSize = 14.sp, color = TwTheme.color.onSurfaceSecondary),
                    )
                }

                item { Divider(color = TwTheme.color.divider) }
                item { HeaderEntry(text = stringResource(id = R.string.settings__biometrics)) }

                item {
                    SwitchEntry(
                        title = stringResource(id = R.string.settings__option_fingerprint),
                        icon = painterResource(id = R.drawable.ic_fingerprint),
                        isChecked = uiState.lockMethod == LockMethod.Biometrics,
                        switch = { isChecked ->
                            if (isChecked) {
                                showBiometricDialog = true
                            } else {
                                viewModel.updateBiometricLock(false)
                            }
                        }
                    )
                }

                item { Divider(color = TwTheme.color.divider) }
            }
        }

        if (showTrailsDialog) {
            ListDialog(
                items = PinTrials.values().map {
                    if (it == PinTrials.NoLimit) {
                        stringResource(id = R.string.settings__no_limit)
                    } else {
                        it.label
                    }
                },
                selected = if (uiState.pinTrials == PinTrials.NoLimit) stringResource(id = R.string.settings__no_limit) else uiState.pinTrials.label,
                onDismiss = { showTrailsDialog = false },
                onSelected = { index, _ -> viewModel.updatePinTrails(PinTrials.values()[index]) }
            )
        }

        if (showTimeoutDialog) {
            ListDialog(
                items = PinTimeout.values().map { stringResource(id = it.label) },
                selected = stringResource(id = uiState.pinTimeout.label),
                onDismiss = { showTimeoutDialog = false },
                onSelected = { index, _ -> viewModel.updatePinTimeout(PinTimeout.values()[index]) }
            )
        }

        if (showBiometricDialog) {
            BiometricDialog(
                activity = activity!! as FragmentActivity,
                titleRes = R.string.biometric_dialog_setup_title,
                cancelRes = R.string.biometric_dialog_setup_cancel,
                onSuccess = {
                    viewModel.updateBiometricLock(true)
                    showBiometricDialog = false
                },
                onFailed = { showBiometricDialog = false },
                onError = { showBiometricDialog = false },
                onDismiss = { showBiometricDialog = false },
            ).show()
        }
    }
}