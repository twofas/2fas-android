package com.twofasapp.feature.home.ui.editservice.advancedsettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.foundation.modal.Modal
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.locale.MdtLocale
import com.twofasapp.locale.R

@Composable
internal fun AdvancedSettingsModal(
    onDismissRequest: () -> Unit,
    service: Service,
) {
    Modal(
        onDismissRequest = onDismissRequest,
        headerText = stringResource(R.string.commons__info),
    ) {
        Content(service = service)
    }
}

@Composable
private fun Content(
    service: Service,
) {
    val strings = MdtLocale.strings

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    ) {
        OptionEntry(
            title = stringResource(R.string.tokens__otp_authentication),
            subtitle = service.authType.name,
        )

        OptionEntry(
            title = strings.addManualAlgorithm,
            subtitle = service.algorithm?.name ?: Service.DefaultAlgorithm.name,
        )

        if (service.authType == Service.AuthType.TOTP || service.authType == Service.AuthType.STEAM) {
            OptionEntry(
                title = strings.addManualRefreshTime,
                subtitle = (service.period ?: Service.DefaultPeriod).toString(),
            )
        }

        if (service.authType == Service.AuthType.HOTP) {
            OptionEntry(
                title = stringResource(R.string.tokens__counter),
                subtitle = (service.hotpCounter ?: 1).toString(),
            )
        }

        OptionEntry(
            title = strings.addManualDigits,
            subtitle = (service.digits ?: Service.DefaultDigits).toString(),
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        Content(service = Service.Preview)
    }
}