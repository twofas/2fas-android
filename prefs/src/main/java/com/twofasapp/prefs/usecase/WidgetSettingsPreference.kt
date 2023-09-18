package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.WidgetSettingsEntity
import com.twofasapp.storage.Preferences

class WidgetSettingsPreference(preferences: Preferences) : PreferenceModel<WidgetSettingsEntity>(preferences) {

    override val key: String = "widgetSettings"
    override val default: WidgetSettingsEntity = WidgetSettingsEntity()

    override val serialize: (WidgetSettingsEntity) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> WidgetSettingsEntity = { jsonSerializer.deserialize(it) }
}