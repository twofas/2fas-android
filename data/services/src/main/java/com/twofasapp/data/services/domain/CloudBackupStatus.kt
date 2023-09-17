package com.twofasapp.data.services.domain

/**
 * Describes if cloud backup is active or not.
 * Created from RemoteBackupStatusEntity.
 */
data class CloudBackupStatus(
    val active: Boolean,
    val account: String? = null,
    val lastSyncMillis: Long = 0L,
    val reference: String? = null,
) {
    val encrypted: Boolean
        get() = reference.isNullOrBlank().not()
}