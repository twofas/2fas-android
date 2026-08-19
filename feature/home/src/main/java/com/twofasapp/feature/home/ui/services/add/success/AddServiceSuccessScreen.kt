package com.twofasapp.feature.home.ui.services.add.success

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.DsServiceModal
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.feature.settings.SettingsLink
import com.twofasapp.core.design.foundation.modal.ModalList
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AddServiceSuccessScreen(
    viewModel: AddServiceSuccessViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.currentActivity

    Column(
        modifier = Modifier
            .background(MdtTheme.color.surface)
            .verticalScroll(rememberScrollState())
            .animateContentSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = MdtLocale.strings.addSuccessTitle,
                style = MdtTheme.typo.regular.xl,
                color = MdtTheme.color.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
            )
        }

        Text(
            text = MdtLocale.strings.addSuccessDescription,
            color = MdtTheme.color.onSurface,
            style = MdtTheme.typo.regular.base,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        uiState.service?.let { service ->
            val serviceState = service.asState()

            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
                    .border(2.dp, Color(0x66BCBBC1), RoundedCornerShape(24.dp))
                    .padding(vertical = 8.dp),
            ) {
                DsServiceModal(
                    state = service.asState(),
                    showNextCode = uiState.showNextCode,
                    hideCodes = false,
                    containerColor = MdtTheme.color.surface,
                    onIncrementCounterClick = { viewModel.incrementHotpCounter(service) },
                    onRevealClick = { viewModel.reveal(service) },
                )
            }

            ModalList {
                SettingsLink(title = MdtLocale.strings.copyToken, icon = MdtIcons.Copy) {
                    serviceState.copyToClipboard(activity, uiState.showNextCode)
                }
            }
        }
    }
}