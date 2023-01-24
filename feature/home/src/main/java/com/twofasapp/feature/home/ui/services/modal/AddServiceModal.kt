package com.twofasapp.feature.home.ui.services.modal

import androidx.compose.runtime.Composable
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.ModalList
import com.twofasapp.designsystem.settings.SettingsLink

@Composable
internal fun AddServiceModal(
    onAddManuallyClick: () -> Unit = {},
    onScanQrClick: () -> Unit = {},
) {
    ModalList {
        SettingsLink(title = "Add manually", icon = TwIcons.Edit) { onAddManuallyClick() }
        SettingsLink(title = "Scan QR code", icon = TwIcons.Qr) { onScanQrClick() }
    }
}