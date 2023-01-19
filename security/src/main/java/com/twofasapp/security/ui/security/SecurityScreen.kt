package com.twofasapp.security.ui.security

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.twofasapp.design.compose.*
import com.twofasapp.design.compose.dialogs.ListDialog
import com.twofasapp.design.theme.divider
import com.twofasapp.design.theme.textPrimary
import com.twofasapp.design.theme.textSecondary
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
            Toolbar(title = stringResource(id = R.string.settings__security)) { activity?.onBackPressed() }
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

                item { Divider(color = MaterialTheme.colors.divider) }
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
                        style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.textSecondary)
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

                item { Divider(color = MaterialTheme.colors.divider) }
                item { HeaderEntry(text = stringResource(id = R.string.settings__app_blocking)) }

                item {
                    SimpleEntry(
                        title = stringResource(id = R.string.settings__limit_of_trials),
                        icon = painterResource(id = R.drawable.ic_limit_trials),
                        subtitleGravity = SubtitleGravity.END,
                        subtitle = uiState.pinTrials.label,
                        click = { showTrailsDialog = true }
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.settings__how_many_attempts_footer),
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp,),
                        style = MaterialTheme.typography.body2.copy(fontSize = 14.sp, color = MaterialTheme.colors.textSecondary),
                    )
                }

                item {
                    SimpleEntry(
                        title = stringResource(id = R.string.settings__block_for),
                        icon = painterResource(id = R.drawable.ic_block_for),
                        subtitleGravity = SubtitleGravity.END,
                        subtitle = uiState.pinTimeout.label,
                        isEnabled = uiState.pinTrials != PinTrials.NoLimit,
                        click = { showTimeoutDialog = true }
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.settings__block_for_footer),
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp,),
                        style = MaterialTheme.typography.body2.copy(fontSize = 14.sp, color = MaterialTheme.colors.textSecondary),
                    )
                }

                item { Divider(color = MaterialTheme.colors.divider) }
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

                item { Divider(color = MaterialTheme.colors.divider) }
            }
        }

        if (showTrailsDialog) {
            ListDialog(
                items = PinTrials.values().map { it.label },
                selected = uiState.pinTrials.label,
                onDismiss = { showTrailsDialog = false },
                onSelected = { index, _ -> viewModel.updatePinTrails(PinTrials.values()[index]) }
            )
        }

        if (showTimeoutDialog) {
            ListDialog(
                items = PinTimeout.values().map { it.label },
                selected = uiState.pinTimeout.label,
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