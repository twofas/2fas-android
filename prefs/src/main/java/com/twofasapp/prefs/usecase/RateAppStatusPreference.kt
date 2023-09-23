package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.RateAppStatus
import com.twofasapp.storage.Preferences
import kotlinx.serialization.encodeToString

class RateAppStatusPreference(preferences: Preferences) : PreferenceModel<RateAppStatus>(preferences) {

    override val key: String = "rateAppStatus"
    override val default: RateAppStatus = RateAppStatus()

    override val serialize: (RateAppStatus) -> String = { jsonSerializer.encodeToString(it) }
    override val deserialize: (String) -> RateAppStatus = { jsonSerializer.decodeFromString(it) }
}