package com.twofasapp.services.ui.advancedsettings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.twofasapp.data.services.domain.Service
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.SwitchEntry
import com.twofasapp.design.compose.SwitchEntryType
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.locale.TwLocale
import com.twofasapp.resources.R
import com.twofasapp.services.ui.EditServiceViewModel

@Composable
internal fun AdvancedSettingsScreen(
    viewModel: EditServiceViewModel
) {

    val service = viewModel.uiState.collectAsState().value.service

    Scaffold(
        topBar = { TwTopAppBar(titleText = stringResource(id = R.string.customization_advanced)) }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                SwitchEntry(
                    title = "TOTP",
                    type = SwitchEntryType.Radio,
                    isChecked = service.authType == Service.AuthType.TOTP,
                    isEnabled = false,
                )
                SwitchEntry(
                    title = "HOTP",
                    type = SwitchEntryType.Radio,
                    isChecked = service.authType == Service.AuthType.HOTP,
                    isEnabled = false,
                )
            }

            item { Divider(color = TwTheme.color.divider) }

            item {
                SimpleEntry(
                    title = TwLocale.strings.addManualAlgorithm,
                    subtitle = service.algorithm?.name.orEmpty(),
                    isEnabled = false,
                )
            }

            if (service.authType == Service.AuthType.TOTP) {
                item {
                    SimpleEntry(
                        title = TwLocale.strings.addManualRefreshTime,
                        subtitle = service.period.toString(),
                        isEnabled = false,
                    )
                }
            }

            if (service.authType == Service.AuthType.HOTP) {
                item {
                    SimpleEntry(
                        title = stringResource(R.string.tokens__counter),
                        subtitle = (service.hotpCounter ?: 1).toString(),
                        isEnabled = false,
                    )
                }
            }

            item {
                SimpleEntry(
                    title = TwLocale.strings.addManualDigits,
                    subtitle = service.digits.toString(),
                    isEnabled = false,
                )
            }

            item { Divider(color = TwTheme.color.divider) }
        }
    }
}
