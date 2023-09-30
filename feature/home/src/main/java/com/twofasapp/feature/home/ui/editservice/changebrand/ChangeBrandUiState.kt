package com.twofasapp.feature.home.ui.editservice.changebrand

import com.twofasapp.feature.home.ui.editservice.BrandIcon

internal data class ChangeBrandUiState(
    val sections: Map<String, List<BrandIcon>> = emptyMap(),
    val scrollTo: Boolean = false,
)