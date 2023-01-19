package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.RemoteBackup
import com.twofasapp.prefs.model.RemoteBackupStatus

class RemoteBackupStatusPreference(preferences: Preferences) : PreferenceModel<RemoteBackupStatus>(preferences) {

    override val key: String = "remoteBackupStatus"
    override val default: RemoteBackupStatus = RemoteBackupStatus(schemaVersion = RemoteBackup.CURRENT_SCHEMA)

    override val serialize: (RemoteBackupStatus) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> RemoteBackupStatus = { jsonSerializer.deserialize(it) }
}