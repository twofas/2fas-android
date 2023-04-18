package com.twofasapp.widgets.configure

import com.twofasapp.prefs.model.ServiceDto

data class WidgetSettingsService(
    val id: Long,
    val serviceDto: ServiceDto,
    val isChecked: Boolean,
    val switchAction: ((entry: WidgetSettingsService, isChecked: Boolean) -> Unit)? = null
) {
    fun toItem() = WidgetSettingsServiceItem(this)
}