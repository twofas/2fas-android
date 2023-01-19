package com.twofasapp.services.backup.models

import com.twofasapp.prefs.model.RemoteBackup

sealed class GetRemoteBackupResult {
    data class Success(
        val backup: com.twofasapp.prefs.model.RemoteBackup
    ) : GetRemoteBackupResult()

    data class Failure(
        val type: RemoteBackupErrorType,
        val throwable: Throwable? = null,
    ) : GetRemoteBackupResult()
}