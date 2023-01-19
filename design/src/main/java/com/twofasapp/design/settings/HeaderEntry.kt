package com.twofasapp.design.settings

data class HeaderEntry(
    val text: String? = null,
    val textRes: Int? = null,
) {
    fun toItem() = HeaderEntryItem(this)
}