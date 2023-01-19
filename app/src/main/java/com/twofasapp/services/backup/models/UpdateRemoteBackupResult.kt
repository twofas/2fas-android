package com.twofasapp.services.backup.models

sealed class UpdateRemoteBackupResult {
    class Success : UpdateRemoteBackupResult()

    data class Failure(
        val type: RemoteBackupErrorType,
        val throwable: Throwable?,
    ) : UpdateRemoteBackupResult()
}