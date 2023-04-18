package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.Groups
import com.twofasapp.storage.Preferences

class GroupsPreference(preferences: Preferences) : PreferenceModel<Groups>(preferences) {

    override val key: String = "groups"
    override val default: Groups = Groups()
    override var useCache: Boolean = false

    override val serialize: (Groups) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> Groups = { jsonSerializer.deserialize(it) }
}