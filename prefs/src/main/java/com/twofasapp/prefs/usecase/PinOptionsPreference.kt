package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.PinOptionsEntity
import com.twofasapp.storage.Preferences
import kotlinx.serialization.encodeToString

class PinOptionsPreference(preferences: Preferences) : PreferenceModel<PinOptionsEntity>(preferences) {

    override val key: String = "pinOptions"
    override val default: PinOptionsEntity = PinOptionsEntity(
        digits = 4,
        trials = 3,
        timeout = 5 * 60 * 1000
    )

    override val serialize: (PinOptionsEntity) -> String = { jsonSerializer.encodeToString(it) }
    override val deserialize: (String) -> PinOptionsEntity = { jsonSerializer.decodeFromString(it) }
}