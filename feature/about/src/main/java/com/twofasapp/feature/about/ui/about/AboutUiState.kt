package com.twofasapp.feature.about.ui.about

import com.twofasapp.data.session.domain.AppSettings

data class AboutUiState(
    val appSettings: AppSettings = AppSettings(),
)
