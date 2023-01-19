package com.twofasapp.backup.domain

import com.twofasapp.base.work.WorkDispatcher

interface SyncBackupWorkDispatcher : WorkDispatcher {
    fun dispatch(trigger: SyncBackupTrigger, password: String? = null)
}