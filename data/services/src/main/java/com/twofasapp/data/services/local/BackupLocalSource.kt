package com.twofasapp.data.services.local

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.serializedPref
import com.twofasapp.prefs.model.RemoteBackup
import com.twofasapp.prefs.model.RemoteBackupKey
import com.twofasapp.prefs.model.RemoteBackupStatusEntity
import kotlinx.coroutines.flow.Flow

class BackupLocalSource(
    dataStoreOwner: DataStoreOwner,
) : DataStoreOwner by dataStoreOwner {

    private val remoteBackupStatus by serializedPref(
        name = "remoteBackupStatus",
        default = RemoteBackupStatusEntity(schemaVersion = RemoteBackup.CURRENT_SCHEMA),
        serializer = RemoteBackupStatusEntity.serializer(),
        encrypted = true,
    )

    private val remoteBackupKey by serializedPref(
        name = "remoteBackupKey",
        default = RemoteBackupKey("", ""),
        serializer = RemoteBackupKey.serializer(),
        encrypted = true,
    )

    fun observeRemoteBackupStatus(): Flow<RemoteBackupStatusEntity> {
        return remoteBackupStatus.asFlow()
    }

    suspend fun getRemoteBackupStatus(): RemoteBackupStatusEntity {
        return remoteBackupStatus.get()
    }

    suspend fun setRemoteBackupStatus(status: RemoteBackupStatusEntity) {
        remoteBackupStatus.set(status)
    }

    suspend fun updateRemoteBackupStatus(action: (RemoteBackupStatusEntity) -> RemoteBackupStatusEntity) {
        remoteBackupStatus.set(action(remoteBackupStatus.get()))
    }

    suspend fun deleteRemoteBackupStatus() {
        remoteBackupStatus.delete()
    }

    suspend fun getRemoteBackupKey(): RemoteBackupKey {
        return remoteBackupKey.get()
    }

    suspend fun setRemoteBackupKey(key: RemoteBackupKey) {
        remoteBackupKey.set(key)
    }

    suspend fun deleteRemoteBackupKey() {
        remoteBackupKey.delete()
    }
}