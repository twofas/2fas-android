package com.twofasapp.feature.home.ui.services.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.ModalList
import com.twofasapp.designsystem.service.TwServiceModal
import com.twofasapp.designsystem.service.ServiceState
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsLink

@Composable
internal fun FocusServiceModal(
    serviceState: ServiceState,
    showNextCode: Boolean = false,
    onEditClick: () -> Unit = {},
    onCopyClick: () -> Unit = {},
) {
    Column {
        TwServiceModal(
            state = serviceState,
            showNextCode = showNextCode,
            containerColor = TwTheme.color.surface,
        )

        SettingsDivider()

        ModalList {
            SettingsLink(title = "Edit", icon = TwIcons.Edit) { onEditClick() }
            SettingsLink(title = "Copy token", icon = TwIcons.Copy) { onCopyClick() }
        }
    }
}