package com.twofasapp.designsystem.service

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.twofasapp.common.domain.Service
import com.twofasapp.designsystem.TwTheme

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
        Service.Tint.Default -> TwTheme.color.surfaceVariant
        Service.Tint.LightBlue -> TwTheme.color.accentLightBlue
        Service.Tint.Indigo -> TwTheme.color.accentIndigo
        Service.Tint.Purple -> TwTheme.color.accentPurple
        Service.Tint.Turquoise -> TwTheme.color.accentTurquoise
        Service.Tint.Green -> TwTheme.color.accentGreen
        Service.Tint.Red -> TwTheme.color.accentRed
        Service.Tint.Orange -> TwTheme.color.accentOrange
        Service.Tint.Yellow -> TwTheme.color.accentYellow
        Service.Tint.Pink -> TwTheme.color.accentPink
        Service.Tint.Brown -> TwTheme.color.accentBrown
        null -> TwTheme.color.surfaceVariant
    }
}