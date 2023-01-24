package com.twofasapp.designsystem.service

import androidx.compose.ui.graphics.Color

data class ServiceState(
    val name: String,
    val info: String? = null,
    val code: String = "",
    val nextCode: String = "",
    val timer: Int = 0,
    val progress: Float = 0f,
    val imageType: ServiceImageType,
    val iconLight: String,
    val iconDark: String,
    val labelText: String?,
    val labelColor: Color,
    val badgeColor: Color = Color.Unspecified,
)