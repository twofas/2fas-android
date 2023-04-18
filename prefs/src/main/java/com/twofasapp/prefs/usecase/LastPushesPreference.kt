package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.LastPushesEntity
import com.twofasapp.storage.Preferences

class LastPushesPreference(preferences: Preferences) : PreferenceModel<LastPushesEntity>(preferences) {
    override val key: String = "lastPushes"
    override val default: LastPushesEntity = LastPushesEntity(emptyList())

    override val serialize: (LastPushesEntity) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> LastPushesEntity = { jsonSerializer.deserialize(it) }
}