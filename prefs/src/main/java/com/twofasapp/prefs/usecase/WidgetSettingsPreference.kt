package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.WidgetSettings
import com.twofasapp.storage.Preferences

class WidgetSettingsPreference(preferences: Preferences) : PreferenceModel<WidgetSettings>(preferences) {

    override val key: String = "widgetSettings"
    override val default: WidgetSettings = WidgetSettings()

    override val serialize: (WidgetSettings) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> WidgetSettings = { jsonSerializer.deserialize(it) }
}