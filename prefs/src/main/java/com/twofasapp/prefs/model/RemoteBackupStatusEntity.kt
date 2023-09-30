package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteBackupStatusEntity(
    @SerialName("syncProvider")
    val syncProvider: SyncProvider = SyncProvider.GOOGLE_DRIVE,
    @SerialName("state")
    val state: State = State.NOT_CONFIGURED,
    @SerialName("account")
    val account: String? = null,
    @SerialName("lastSyncMillis")
    val lastSyncMillis: Long = 0,
    @SerialName("schemaVersion")
    val schemaVersion: Int?,
    @SerialName("reference")
    val reference: String? = null,
) {
    enum class SyncProvider {
        GOOGLE_DRIVE
    }

    enum class State {
        NOT_CONFIGURED,
        NOT_ACTIVE,
        ACTIVE,
    }
}
