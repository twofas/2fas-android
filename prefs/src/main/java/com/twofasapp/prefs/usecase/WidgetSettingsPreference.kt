package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.WidgetSettingsEntity
import com.twofasapp.storage.Preferences
import kotlinx.serialization.encodeToString

class WidgetSettingsPreference(preferences: Preferences) : PreferenceModel<WidgetSettingsEntity>(preferences) {

    override val key: String = "widgetSettings"
    override val default: WidgetSettingsEntity = WidgetSettingsEntity()

    override val serialize: (WidgetSettingsEntity) -> String = { jsonSerializer.encodeToString(it) }
    override val deserialize: (String) -> WidgetSettingsEntity = { jsonSerializer.decodeFromString(it) }
}