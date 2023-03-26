package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.RecentlyDeleted
import com.twofasapp.storage.Preferences

class RecentlyDeletedPreference(preferences: Preferences) : PreferenceModel<RecentlyDeleted>(preferences) {

    override val key: String = "recentlyDeleted"
    override val default: RecentlyDeleted = RecentlyDeleted(emptyList())

    override val serialize: (RecentlyDeleted) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> RecentlyDeleted = { jsonSerializer.deserialize(it) }
}