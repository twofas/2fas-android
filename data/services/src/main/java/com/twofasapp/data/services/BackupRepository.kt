package com.twofasapp.data.services

import android.net.Uri
import com.twofasapp.data.services.domain.BackupContent
import com.twofasapp.data.services.domain.BackupContentCreateResult
import com.twofasapp.data.services.domain.CloudBackupGetResult
import com.twofasapp.data.services.domain.CloudBackupStatus
import com.twofasapp.data.services.domain.CloudBackupUpdateResult
import com.twofasapp.data.services.domain.CloudSyncStatus
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.exceptions.DecryptWrongPassword
import kotlinx.coroutines.flow.Flow

interface BackupRepository {
    /**
     * Dispatch worker which syncs local services with cloud.
     */
    fun dispatchCloudSync(trigger: CloudSyncTrigger, password: String? = null)

    /**
     * Dispatch worker which wipes data from cloud and revoke access.
     */
    fun dispatchWipeData()

    /**
     * Create BackupContent from local database.
     * @return plain or encrypted content (depends on provided params)
     */
    suspend fun createBackupContent(
        password: String? = null,
        keyEncoded: String? = null,
        saltEncoded: String? = null,
        account: String? = null,
    ): BackupContentCreateResult

    /**
     * Create BackupContent from local database.
     * @return plain or encrypted content (depends on provided params) serialized to JSON string
     */
    suspend fun createBackupContentSerialized(
        password: String? = null,
        keyEncoded: String? = null,
        saltEncoded: String? = null,
        account: String? = null,
    ): String

    /**
     * Read BackupContent from a file.
     * @return plain or encrypted content
     * @exception FileTooBigException
     */
    suspend fun readBackupContent(
        fileUri: Uri,
    ): BackupContent

    /**
     * Decrypt BackupContent using password or provided encryption key.
     * @return plain content
     * @exception DecryptWrongPassword
     */
    suspend fun decryptBackupContent(
        backupContent: BackupContent,
        password: String? = null,
        keyEncoded: String? = null,
    ): BackupContent

    /**
     * Import BackupContent into local database.
     * If service/group already exists on local storage it will be skipped.
     */
    suspend fun import(backupContent: BackupContent)

    /**
     * Mark cloud sync as active.
     */
    suspend fun setCloudSyncActive(email: String)

    /**
     * Mark cloud sync as not configured.
     */
    suspend fun setCloudSyncNotConfigured()

    /**
     * Get BackupContent from cloud provider.
     * @return Success or Failure. Handle CloudBackupError.
     */
    suspend fun getCloudBackup(password: String? = null): CloudBackupGetResult

    /**
     * Update BackupContent to cloud provider.
     * @return Success or Failure. Handle CloudBackupError.
     */
    suspend fun updateCloudBackup(
        password: String? = null,
        keyEncoded: String? = null,
        saltEncoded: String? = null,
        firstConnect: Boolean,
        updatedAt: Long,
    ): CloudBackupUpdateResult

    /**
     * Check if cloud backup password is correct.
     * @return true if correct
     */
    suspend fun checkCloudBackupPassword(password: String?): Boolean

    /**
     * Observe cloud backup status
     */
    fun observeCloudBackupStatus(): Flow<CloudBackupStatus>

    /**
     * Observe cloud sync status
     */
    fun observeCloudSyncStatus(): Flow<CloudSyncStatus>

    /**
     * Publish clouds sync status
     */
    fun publishCloudSyncStatus(status: CloudSyncStatus)

    /**
     * Set password for cloud sync. The password is used to create local encryption key
     * and used for cloud sync (once enabled).
     */
    fun setPasswordForCloudSync(password: String?)

    fun observePasswordForCloudSync(): Flow<String?>
}