package com.twofasapp.feature.home.ui.services.modal

import androidx.compose.runtime.Composable
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.ModalList
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.locale.TwLocale

@Composable
internal fun AddServiceModal(
    onAddManuallyClick: () -> Unit = {},
    onScanQrClick: () -> Unit = {},
) {
    ModalList {
        SettingsLink(title = TwLocale.strings.addManually, icon = TwIcons.Edit) { onAddManuallyClick() }
        SettingsLink(title = TwLocale.strings.addQr, icon = TwIcons.Qr) { onScanQrClick() }
    }
}