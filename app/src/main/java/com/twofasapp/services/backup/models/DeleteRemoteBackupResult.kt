package com.twofasapp.services.backup.models

sealed class DeleteRemoteBackupResult {
    class Success : DeleteRemoteBackupResult()

    data class Failure(
        val type: RemoteBackupErrorType,
        val throwable: Throwable?,
    ) : DeleteRemoteBackupResult()
}