package com.twofasapp.feature.home.ui.services.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.ModalList
import com.twofasapp.designsystem.service.DsServiceModal
import com.twofasapp.designsystem.service.ServiceState
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.locale.TwLocale

@Composable
internal fun FocusServiceModal(
    serviceState: ServiceState,
    showNextCode: Boolean = false,
    onEditClick: () -> Unit = {},
    onCopyClick: () -> Unit = {},
    onIncrementCounterClick: () -> Unit = {},
) {
    Column {
        DsServiceModal(
            state = serviceState,
            showNextCode = showNextCode,
            containerColor = TwTheme.color.surface,
            onIncrementCounterClick = onIncrementCounterClick
        )

        SettingsDivider()

        ModalList {
            SettingsLink(title = TwLocale.strings.editService, icon = TwIcons.Edit) { onEditClick() }
            SettingsLink(title = TwLocale.strings.copyToken, icon = TwIcons.Copy) { onCopyClick() }
        }
    }
}