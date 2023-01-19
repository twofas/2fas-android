package com.twofasapp.widgets.configure

data class WidgetSettingsService(
    val id: Long,
    val name: String,
    val isChecked: Boolean,
    val switchAction: ((entry: WidgetSettingsService, isChecked: Boolean) -> Unit)? = null
) {
    fun toItem() = WidgetSettingsServiceItem(this)
}