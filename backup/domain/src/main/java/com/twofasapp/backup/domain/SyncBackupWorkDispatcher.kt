package com.twofasapp.backup.domain

import com.twofasapp.di.WorkDispatcher

interface SyncBackupWorkDispatcher : WorkDispatcher {
    fun tryDispatch(trigger: SyncBackupTrigger, password: String? = null)
}