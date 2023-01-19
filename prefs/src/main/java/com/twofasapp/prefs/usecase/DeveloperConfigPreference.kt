package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.DeveloperConfigEntity

class DeveloperConfigPreference(preferences: Preferences) : PreferenceModel<DeveloperConfigEntity>(preferences) {

    override val key: String = "developerConfig"
    override val default: DeveloperConfigEntity = DeveloperConfigEntity()

    override val serialize: (DeveloperConfigEntity) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> DeveloperConfigEntity = { jsonSerializer.deserialize(it) }
}