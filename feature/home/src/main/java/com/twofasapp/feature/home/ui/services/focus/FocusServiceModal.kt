package com.twofasapp.feature.home.ui.services.focus

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.DsServiceModal
import com.twofasapp.core.design.feature.items.ServiceAuthType
import com.twofasapp.core.design.feature.items.ServiceImageType
import com.twofasapp.core.design.feature.items.ServiceState
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.feature.settings.SettingsDivider
import com.twofasapp.core.design.feature.settings.SettingsLink
import com.twofasapp.core.design.foundation.modal.Modal
import com.twofasapp.core.design.foundation.modal.ModalList
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel

object FocusServiceModalNavArg {
    val ServiceId = navArgument("id") { type = NavType.LongType }
}

@Composable
fun FocusServiceModal(
    viewModel: FocusServiceViewModel = koinViewModel(),
    openService: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.currentActivity
    val serviceState = uiState.service?.asState() ?: ServiceState(
        name = " ",
        info = " ",
        code = "      ",
        nextCode = "",
        timer = 30,
        hotpCounter = null,
        hotpCounterEnabled = false,
        progress = 1f,
        imageType = ServiceImageType.Icon,
        authType = ServiceAuthType.Totp,
        iconLight = "",
        iconDark = "",
        labelText = null,
        labelColor = Color.Unspecified,
        badgeColor = Color.Unspecified,
        revealed = true,
    )

    Modal {
        Column {
            DsServiceModal(
                state = serviceState,
                showNextCode = uiState.showNextCode,
                hideCodes = uiState.hideCodes,
                containerColor = MdtTheme.color.surface,
                onIncrementCounterClick = { viewModel.incrementCounter() },
                onRevealClick = { viewModel.reveal() },
            )

            SettingsDivider()

            ModalList {
                SettingsLink(title = MdtLocale.strings.editService, icon = MdtIcons.Edit) {
                    uiState.service?.id?.let(openService)
                }
                SettingsLink(title = MdtLocale.strings.copyToken, icon = MdtIcons.Copy) {
                    serviceState.copyToClipboard(
                        activity,
                        uiState.showNextCode,
                    )
                }
            }
        }
    }
}