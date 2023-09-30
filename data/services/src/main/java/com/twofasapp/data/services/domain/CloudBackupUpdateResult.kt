package com.twofasapp.data.services.domain

sealed interface CloudBackupUpdateResult {
    data object Success : CloudBackupUpdateResult

    data class Failure(
        val error: CloudSyncError,
    ) : CloudBackupUpdateResult
}