package com.twofasapp.usecases.backup.model

import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.services.backup.models.RemoteBackupErrorType

sealed class SyncStatus {
    object Default : SyncStatus()
    data class Synced(
        val trigger: SyncBackupTrigger,
    ) : SyncStatus()

    object Syncing : SyncStatus()
    data class Error(
        val type: RemoteBackupErrorType,
        val trigger: SyncBackupTrigger,
    ) : SyncStatus() {

        fun shouldShowError() = trigger != SyncBackupTrigger.REMOVE_PASSWORD
                && trigger != SyncBackupTrigger.SET_PASSWORD
                && trigger != SyncBackupTrigger.WIPE_DATA
    }
}
