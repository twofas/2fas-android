package com.twofasapp.feature.home.ui.services.focus

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.Modal
import com.twofasapp.designsystem.common.ModalList
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.service.DsServiceModal
import com.twofasapp.designsystem.service.ServiceAuthType
import com.twofasapp.designsystem.service.ServiceImageType
import com.twofasapp.designsystem.service.ServiceState
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.feature.home.ui.services.asState
import com.twofasapp.locale.TwLocale
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
                containerColor = TwTheme.color.surface,
                onIncrementCounterClick = { viewModel.incrementCounter() },
                onRevealClick = { viewModel.reveal() }
            )

            SettingsDivider()

            ModalList {
                SettingsLink(title = TwLocale.strings.editService, icon = TwIcons.Edit) {
                    uiState.service?.id?.let(openService)
                }
                SettingsLink(title = TwLocale.strings.copyToken, icon = TwIcons.Copy) {
                    serviceState.copyToClipboard(
                        activity,
                        uiState.showNextCode
                    )
                }
            }
        }
    }
}