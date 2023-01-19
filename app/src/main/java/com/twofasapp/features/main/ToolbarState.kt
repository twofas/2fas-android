package com.twofasapp.features.main

data class ToolbarState(
    val titleRes: Int?,
    val isTitleCentered: Boolean,
    val iconRes: Int,
    val iconContentDescription: String,
    val iconAction: () -> Unit,
)