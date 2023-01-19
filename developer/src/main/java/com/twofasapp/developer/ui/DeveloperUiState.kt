package com.twofasapp.developer.ui

import com.twofasapp.developer.domain.model.LastPush
import com.twofasapp.developer.domain.model.LastScannedQr
import com.twofasapp.featuretoggle.domain.model.FeatureToggle

internal data class DeveloperUiState(
    val featureToggles: Map<FeatureToggle, Boolean> = emptyMap(),
    val fcmToken: String = "",
    val lastScannedQr: LastScannedQr = LastScannedQr(""),
    val lastPushes: List<LastPush> = emptyList(),
)