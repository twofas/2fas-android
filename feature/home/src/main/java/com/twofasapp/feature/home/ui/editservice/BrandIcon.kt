package com.twofasapp.feature.home.ui.editservice

data class BrandIcon(
    val name: String,
    val iconCollectionId: String,
    val tags: List<String> = emptyList(),
)