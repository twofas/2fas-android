package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.RemoteBackupKey
import com.twofasapp.storage.Preferences
import kotlinx.serialization.encodeToString

class RemoteBackupKeyPreference(preferences: Preferences) : PreferenceModel<RemoteBackupKey>(preferences) {

    override val key: String = "remoteBackupKey"
    override val default: RemoteBackupKey = RemoteBackupKey("", "")

    override val serialize: (RemoteBackupKey) -> String = { jsonSerializer.encodeToString(it) }
    override val deserialize: (String) -> RemoteBackupKey = { jsonSerializer.decodeFromString(it) }
}