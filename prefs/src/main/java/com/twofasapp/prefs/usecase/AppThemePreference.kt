package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.AppTheme

class AppThemePreference(preferences: Preferences) : PreferenceModel<AppTheme>(preferences) {

    override val key: String = "appTheme"
    override val default: AppTheme = AppTheme.AUTO

    override val serialize: (AppTheme) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> AppTheme = { jsonSerializer.deserialize(it) }
}