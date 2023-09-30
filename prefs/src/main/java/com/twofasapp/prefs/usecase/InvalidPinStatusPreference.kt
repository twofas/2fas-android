package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.InvalidPinStatusEntity
import com.twofasapp.storage.Preferences
import kotlinx.serialization.encodeToString

class InvalidPinStatusPreference(preferences: Preferences) : PreferenceModel<InvalidPinStatusEntity>(preferences) {

    override val key: String = "invalidPinStatus"
    override val default: InvalidPinStatusEntity = InvalidPinStatusEntity()

    override val serialize: (InvalidPinStatusEntity) -> String = { jsonSerializer.encodeToString(it) }
    override val deserialize: (String) -> InvalidPinStatusEntity = { jsonSerializer.decodeFromString(it) }
}