package com.twofasapp.data.services.domain

sealed interface CloudBackupGetResult {
    data class Success(
        val backupContent: BackupContent,
    ) : CloudBackupGetResult

    data class Failure(
        val error: CloudSyncError,
    ) : CloudBackupGetResult
}