package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.LockMethodEntity

class LockMethodPreference(preferences: Preferences) : PreferenceModel<LockMethodEntity>(preferences) {

    override val key: String = "lockStatus"
    override val default: LockMethodEntity = LockMethodEntity.NO_LOCK

    override val serialize: (LockMethodEntity) -> String = { it.name }
    override val deserialize: (String) -> LockMethodEntity = { LockMethodEntity.valueOf(it) }
}