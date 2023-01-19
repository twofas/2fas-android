package com.twofasapp.features.main

data class DrawerEntry(
    val iconRes: Int,
    val titleRes: Int,
    val badge: Badge = Badge.None,
    val isSelected: Boolean = false,
    val endIconRes: Int? = null,
    val onClick: () -> Unit
) {
    sealed class Badge {
        object None : Badge()
        object Dot : Badge()
        object DotIcon : Badge()
        data class Label(val text: String) : Badge()
    }
}