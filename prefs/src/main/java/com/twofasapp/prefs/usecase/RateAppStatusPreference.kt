package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.RateAppStatus

class RateAppStatusPreference(preferences: Preferences) : PreferenceModel<RateAppStatus>(preferences) {

    override val key: String = "rateAppStatus"
    override val default: RateAppStatus = RateAppStatus()

    override val serialize: (RateAppStatus) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> RateAppStatus = { jsonSerializer.deserialize(it) }
}