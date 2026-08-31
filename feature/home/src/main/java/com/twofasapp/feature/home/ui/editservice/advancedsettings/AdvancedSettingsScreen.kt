package com.twofasapp.feature.home.ui.editservice.advancedsettings

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
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.foundation.outline.HorizontalLine
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.locale.MdtLocale
import com.twofasapp.locale.R

@Composable
internal fun AdvancedSettingsScreen(
    viewModel: com.twofasapp.feature.home.ui.editservice.EditServiceViewModel,
) {
    val service = viewModel.uiState.collectAsState().value.service

    Content(service = service)
}

@Composable
private fun Content(
    service: Service,
) {
    Scaffold(
        topBar = { TopAppBar(title = stringResource(id = R.string.customization_advanced)) },
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                OptionEntry(
                    title = "TOTP",
                    enabled = false,
                    content = {
                        RadioButton(
                            selected = service.authType == Service.AuthType.TOTP,
                            enabled = false,
                            onClick = {},
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MdtTheme.color.primary,
                                unselectedColor = Color(0xFF585858),
                            ),
                        )
                    },
                )
            }

            item {
                OptionEntry(
                    title = "HOTP",
                    enabled = false,
                    content = {
                        RadioButton(
                            selected = service.authType == Service.AuthType.HOTP,
                            enabled = false,
                            onClick = {},
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MdtTheme.color.primary,
                                unselectedColor = Color(0xFF585858),
                            ),
                        )
                    },
                )
            }

            item {
                OptionEntry(
                    title = "STEAM",
                    enabled = false,
                    content = {
                        RadioButton(
                            selected = service.authType == Service.AuthType.STEAM,
                            enabled = false,
                            onClick = {},
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MdtTheme.color.primary,
                                unselectedColor = Color(0xFF585858),
                            ),
                        )
                    },
                )
            }

            item { HorizontalLine() }

            item {
                OptionEntry(
                    title = MdtLocale.strings.addManualAlgorithm,
                    subtitle = service.algorithm?.name ?: Service.DefaultAlgorithm.name,
                    enabled = false,
                )
            }

            if (service.authType == Service.AuthType.TOTP || service.authType == Service.AuthType.STEAM) {
                item {
                    OptionEntry(
                        title = MdtLocale.strings.addManualRefreshTime,
                        subtitle = (service.period ?: Service.DefaultPeriod).toString(),
                        enabled = false,
                    )
                }
            }

            if (service.authType == Service.AuthType.HOTP) {
                item {
                    OptionEntry(
                        title = stringResource(R.string.tokens__counter),
                        subtitle = (service.hotpCounter ?: 1).toString(),
                        enabled = false,
                    )
                }
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.addManualDigits,
                    subtitle = (service.digits ?: Service.DefaultDigits).toString(),
                    enabled = false,
                )
            }

            item { HorizontalLine() }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(service = Service.Preview)
    }
}