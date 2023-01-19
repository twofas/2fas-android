package com.twofasapp.entity

import com.twofasapp.backup.domain.SyncBackupTrigger

sealed class SyncBackupResult {
    class Success : SyncBackupResult()

    data class Failure(val trigger: SyncBackupTrigger) : SyncBackupResult()
}