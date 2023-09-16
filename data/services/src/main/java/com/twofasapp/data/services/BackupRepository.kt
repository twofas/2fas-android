package com.twofasapp.data.services

import android.net.Uri
import com.twofasapp.data.services.domain.BackupContent

interface BackupRepository {
    suspend fun createBackupContent(
        password: String? = null,
        keyEncoded: String? = null,
        saltEncoded: String? = null,
    ): BackupContent

    suspend fun createBackupContentSerialized(
        password: String? = null,
        keyEncoded: String? = null,
        saltEncoded: String? = null,
    ): String

    suspend fun readBackupContent(
        fileUri: Uri,
        password: String? = null,
    ): BackupContent

    suspend fun decryptBackupContent(
        backupContent: BackupContent,
        password: String,
    ): BackupContent

    suspend fun import(backupContent: BackupContent)
}