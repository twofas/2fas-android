package com.twofasapp.backup.domain

import com.twofasapp.di.WorkDispatcher

interface SyncBackupWorkDispatcher : WorkDispatcher {
    fun dispatch(trigger: SyncBackupTrigger, password: String? = null)
}