package com.twofasapp.feature.home.ui.services

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.twofasapp.data.services.domain.Service
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.service.ServiceImageType
import com.twofasapp.designsystem.service.ServiceState

@Composable
fun Service.asState(): ServiceState {
    return ServiceState(
        name = name,
        info = info,
        code = code?.current.orEmpty(),
        nextCode = code?.next.orEmpty(),
        timer = code?.timer ?: 0,
        progress = code?.progress ?: 0f,
        imageType = when (imageType) {
            Service.ImageType.IconCollection -> ServiceImageType.Icon
            Service.ImageType.Label -> ServiceImageType.Label
        },
        iconLight = iconLight,
        iconDark = iconDark,
        labelText = labelText,
        labelColor = labelColor.asState(),
        badgeColor = badgeColor.asState()
    )
}

@Composable
private fun Service.Tint?.asState(): Color {
    return when (this) {
        Service.Tint.Default -> TwTheme.color.surfaceVariant
        Service.Tint.LightBlue -> TwTheme.color.accentLightBlue
        Service.Tint.Indigo -> TwTheme.color.accentIndigo
        Service.Tint.Purple -> TwTheme.color.accentPurple
        Service.Tint.Turquoise -> TwTheme.color.accentTurquoise
        Service.Tint.Green -> TwTheme.color.accentGreen
        Service.Tint.Red -> TwTheme.color.accentRed
        Service.Tint.Orange -> TwTheme.color.accentOrange
        Service.Tint.Yellow -> TwTheme.color.accentYellow
        null -> TwTheme.color.surfaceVariant
    }
}