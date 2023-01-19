package com.twofasapp.services.ui.advancedsettings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.SwitchEntry
import com.twofasapp.design.compose.SwitchEntryType
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.compose.dialogs.InputDialog
import com.twofasapp.design.compose.dialogs.InputType
import com.twofasapp.design.compose.dialogs.ListDialog
import com.twofasapp.design.theme.divider
import com.twofasapp.navigation.ServiceRouter
import com.twofasapp.resources.R
import com.twofasapp.services.domain.model.Service
import com.twofasapp.services.domain.model.Service.AuthType.HOTP
import com.twofasapp.services.domain.model.Service.AuthType.TOTP
import com.twofasapp.services.ui.ServiceViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
internal fun AdvancedSettingsScreen(
    viewModel: ServiceViewModel = getViewModel(),
    router: ServiceRouter = get()
) {

    val service = viewModel.uiState.collectAsState().value.service
    val showAlgorithmDialog = remember { mutableStateOf(false) }
    val showPeriodDialog = remember { mutableStateOf(false) }
    val showDigitsDialog = remember { mutableStateOf(false) }
    val showCounterDialog = remember { mutableStateOf(false) }
    val isEnabled = service.id == 0L

    Scaffold(
        topBar = { Toolbar(title = stringResource(id = R.string.customization_advanced)) { router.navigateBack() } }
    ) { padding ->

        LazyColumn {
            item {
                SwitchEntry(
                    title = "TOTP",
                    type = SwitchEntryType.Radio,
                    isChecked = service.authType == TOTP,
                    isEnabled = isEnabled,
                    switch = { isChecked ->
                        if (isChecked) {
                            viewModel.updateAuthType(TOTP)
                        }
                    }
                )
                SwitchEntry(
                    title = "HOTP",
                    type = SwitchEntryType.Radio,
                    isChecked = service.authType == HOTP,
                    isEnabled = isEnabled,
                    switch = { isChecked ->
                        if (isChecked) {
                            viewModel.updateAuthType(HOTP)
                        }
                    }
                )
            }

            item { Divider(color = MaterialTheme.colors.divider) }

            item {
                SimpleEntry(
                    title = stringResource(R.string.tokens__algorithm),
                    subtitle = service.otp.algorithm.name,
                    isEnabled = isEnabled,
                    click = {
                        if (service.authType == TOTP) {
                            showAlgorithmDialog.value = true
                        }
                    }
                )
            }

            if (service.authType == TOTP) {
                item {
                    SimpleEntry(
                        title = stringResource(R.string.tokens__refresh_time),
                        subtitle = service.otp.period.toString(),
                        isEnabled = isEnabled,
                        click = { showPeriodDialog.value = true }
                    )
                }
            }

            if (service.authType == HOTP) {
                item {
                    SimpleEntry(
                        title = if (isEnabled) stringResource(R.string.tokens__initial_counter) else stringResource(R.string.tokens__counter),
                        subtitle = (service.otp.hotpCounter ?: 1).toString(),
                        isEnabled = isEnabled,
                        click = { showCounterDialog.value = true }
                    )
                }
            }

            item {
                SimpleEntry(
                    title = stringResource(R.string.tokens__number_of_digits),
                    subtitle = service.otp.digits.toString(),
                    isEnabled = isEnabled,
                    click = { showDigitsDialog.value = true }
                )
            }

            item { Divider(color = MaterialTheme.colors.divider) }
        }

        if (showAlgorithmDialog.value) {
            ListDialog(
                items = Service.Algorithm.values().map { it.name },
                selected = service.otp.algorithm.name,
                onDismiss = { showAlgorithmDialog.value = false },
                onSelected = { _, value -> viewModel.updateAlgorithm(Service.Algorithm.valueOf(value)) }
            )
        }

        if (showPeriodDialog.value) {
            ListDialog(
                items = listOf("30", "60", "90"),
                selected = service.otp.period.toString(),
                onDismiss = { showPeriodDialog.value = false },
                onSelected = { _, value -> viewModel.updatePeriod(value.toInt()) }
            )
        }

        if (showDigitsDialog.value) {
            ListDialog(
                items = listOf("6", "7", "8"),
                selected = service.otp.digits.toString(),
                onDismiss = { showDigitsDialog.value = false },
                onSelected = { _, value -> viewModel.updateDigits(value.toInt()) }
            )
        }

        if (showCounterDialog.value) {
            InputDialog(
                hint = stringResource(id = R.string.tokens__initial_counter),
                allowEmpty = false,
                maxLength = 6,
                prefill = (service.otp.hotpCounter ?: 1).toString(),
                inputType = InputType.NumberInteger,
                onDismiss = { showCounterDialog.value = false },
                onPositive = { viewModel.updateInitialCounter(it.toInt()) }
            )
        }
    }
}
