package com.twofasapp.data.services

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
}