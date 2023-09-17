package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.RemoteBackup
import com.twofasapp.prefs.model.RemoteBackupStatusEntity
import com.twofasapp.storage.Preferences

class RemoteBackupStatusPreference(preferences: Preferences) : PreferenceModel<RemoteBackupStatusEntity>(preferences) {

    override val key: String = "remoteBackupStatus"
    override val default: RemoteBackupStatusEntity = RemoteBackupStatusEntity(schemaVersion = RemoteBackup.CURRENT_SCHEMA)

    override val serialize: (RemoteBackupStatusEntity) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> RemoteBackupStatusEntity = { jsonSerializer.deserialize(it) }
}