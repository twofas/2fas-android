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
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.ModalList
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.service.DsServiceModal
import com.twofasapp.designsystem.service.asState
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AddServiceSuccessScreen(
    viewModel: AddServiceSuccessViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.currentActivity

    Column(
        modifier = Modifier
            .background(TwTheme.color.surface)
            .verticalScroll(rememberScrollState())
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = TwLocale.strings.addSuccessTitle,
                style = TwTheme.typo.title,
                color = TwTheme.color.onSurfacePrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
            )
        }

        Text(
            text = TwLocale.strings.addSuccessDescription,
            color = TwTheme.color.onSurfacePrimary,
            style = TwTheme.typo.body1,
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
                    .padding(vertical = 8.dp)
            ) {

                DsServiceModal(
                    state = service.asState(),
                    showNextCode = uiState.showNextCode,
                    hideCodes = false,
                    containerColor = TwTheme.color.surface,
                    onIncrementCounterClick = { viewModel.incrementHotpCounter(service) },
                    onRevealClick = { viewModel.reveal(service) }
                )
            }

            ModalList {
                SettingsLink(title = TwLocale.strings.copyToken, icon = TwIcons.Copy) {
                    serviceState.copyToClipboard(activity, uiState.showNextCode)
                }
            }
        }
    }
}