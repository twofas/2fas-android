package com.twofasapp.feature.home.ui.services.add.success

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.DsServiceModal
import com.twofasapp.core.design.feature.items.ServiceState
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.core.design.theme.RoundedShape24
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun AddServiceSuccessContent(
    serviceId: Long,
    viewModel: AddServiceSuccessViewModel = koinViewModel { parametersOf(serviceId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.currentActivity
    val serviceState = uiState.service?.asState()

    Content(
        uiState = uiState,
        serviceState = serviceState,
        onCopyClick = { serviceState?.copyToClipboard(activity, uiState.showNextCode) },
        onIncrementCounterClick = { viewModel.incrementHotpCounter() },
        onRevealClick = { viewModel.reveal() },
    )
}

@Composable
private fun Content(
    uiState: AddServiceSuccessUiState,
    serviceState: ServiceState?,
    onCopyClick: () -> Unit = {},
    onIncrementCounterClick: () -> Unit = {},
    onRevealClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier,
    ) {
        Space(16.dp)

        Text(
            text = MdtLocale.strings.addSuccessTitle,
            style = MdtTheme.typo.lg.medium,
            color = MdtTheme.color.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
        )

        Space(12.dp)

        Text(
            text = MdtLocale.strings.addSuccessDescription,
            color = MdtTheme.color.onSurfaceVariant,
            style = MdtTheme.typo.sm.normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Space(16.dp)

        if (serviceState != null) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(1.dp, MdtTheme.color.outlineVariant, RoundedShape24),
            ) {
                DsServiceModal(
                    state = serviceState,
                    showNextCode = uiState.showNextCode,
                    hideCodes = false,
                    containerColor = MdtTheme.color.surface,
                    onIncrementCounterClick = onIncrementCounterClick,
                    onRevealClick = onRevealClick,
                )
            }

            Space(16.dp)

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                style = ButtonStyle.Tonal,
                text = MdtLocale.strings.copyToken,
                onClick = onCopyClick,
            )

            Space(24.dp)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            uiState = AddServiceSuccessUiState(),
            serviceState = ServiceState.Empty.copy(name = "Google", info = "john@gmail.com", code = "123456"),
        )
    }
}