package com.twofasapp.core.design.feature.items

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtTheme

@Composable
fun Service.asState(): ServiceState {
    return ServiceState(
        name = name,
        info = info,
        code = code?.current.orEmpty(),
        nextCode = code?.next.orEmpty(),
        timer = code?.timer ?: 0,
        progress = code?.progress ?: 0f,
        hotpCounter = hotpCounter,
        hotpCounterEnabled = if (hotpCounterTimestamp == null) true else hotpCounterTimestamp!! + 5000L < System.currentTimeMillis(),
        imageType = when (imageType) {
            Service.ImageType.IconCollection -> ServiceImageType.Icon
            Service.ImageType.Label -> ServiceImageType.Label
        },
        authType = when (authType) {
            Service.AuthType.TOTP -> ServiceAuthType.Totp
            Service.AuthType.HOTP -> ServiceAuthType.Hotp
            Service.AuthType.STEAM -> ServiceAuthType.Steam
        },
        iconLight = iconLight,
        iconDark = iconDark,
        labelText = labelText,
        labelColor = labelColor.asColor(),
        badgeColor = badgeColor.asColor(),
        revealed = revealTimestamp?.let { it + 10000L > System.currentTimeMillis() } ?: false,
    )
}

@Composable
fun Service.Tint?.asColor(): Color {
    return when (this) {
        Service.Tint.Default -> MdtTheme.color.surfaceVariant
        Service.Tint.LightBlue -> MdtTheme.color.accentLightBlue
        Service.Tint.Indigo -> MdtTheme.color.accentIndigo
        Service.Tint.Purple -> MdtTheme.color.accentPurple
        Service.Tint.Turquoise -> MdtTheme.color.accentTurquoise
        Service.Tint.Green -> MdtTheme.color.accentGreen
        Service.Tint.Red -> MdtTheme.color.accentRed
        Service.Tint.Orange -> MdtTheme.color.accentOrange
        Service.Tint.Yellow -> MdtTheme.color.accentYellow
        Service.Tint.Pink -> MdtTheme.color.accentPink
        Service.Tint.Brown -> MdtTheme.color.accentBrown
        null -> MdtTheme.color.surfaceVariant
    }
}