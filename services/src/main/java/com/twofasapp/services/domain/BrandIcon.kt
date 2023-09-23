package com.twofasapp.services.domain

data class BrandIcon(
    val name: String,
    val iconCollectionId: String,
    val tags: List<String> = emptyList(),
)