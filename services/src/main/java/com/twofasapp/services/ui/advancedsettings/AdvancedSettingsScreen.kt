package com.twofasapp.services.ui.advancedsettings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.twofasapp.common.domain.Service
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsLink
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
                SettingsLink(
                    title = "TOTP",
                    enabled = false,
                    endContent = {
                        RadioButton(
                            selected = service.authType == Service.AuthType.TOTP,
                            enabled = false,
                            onClick = {},
                            colors = RadioButtonDefaults.colors(
                                selectedColor = TwTheme.color.primary,
                                unselectedColor = Color(0xFF585858),
                            )
                        )
                    }
                )
            }

            item {
                SettingsLink(
                    title = "HOTP",
                    enabled = false,
                    endContent = {
                        RadioButton(
                            selected = service.authType == Service.AuthType.HOTP,
                            enabled = false,
                            onClick = {},
                            colors = RadioButtonDefaults.colors(
                                selectedColor = TwTheme.color.primary,
                                unselectedColor = Color(0xFF585858),
                            )
                        )
                    }
                )
            }


            item { SettingsDivider() }

            item {
                SettingsLink(
                    title = TwLocale.strings.addManualAlgorithm,
                    subtitle = service.algorithm?.name.orEmpty(),
                    enabled = false,
                )
            }

            if (service.authType == Service.AuthType.TOTP) {
                item {
                    SettingsLink(
                        title = TwLocale.strings.addManualRefreshTime,
                        subtitle = service.period.toString(),
                        enabled = false,
                    )
                }
            }

            if (service.authType == Service.AuthType.HOTP) {
                item {
                    SettingsLink(
                        title = stringResource(R.string.tokens__counter),
                        subtitle = (service.hotpCounter ?: 1).toString(),
                        enabled = false,
                    )
                }
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.addManualDigits,
                    subtitle = service.digits.toString(),
                    enabled = false,
                )
            }

            item { SettingsDivider() }
        }
    }
}
