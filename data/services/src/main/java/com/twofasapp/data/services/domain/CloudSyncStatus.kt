package com.twofasapp.data.services.domain

sealed interface CloudSyncStatus {
    data object Default : CloudSyncStatus

    data object Syncing : CloudSyncStatus

    data class Synced(val trigger: CloudSyncTrigger) : CloudSyncStatus

    data class Error(val trigger: CloudSyncTrigger, val error: CloudSyncError) : CloudSyncStatus {
        fun shouldShowError() = trigger != CloudSyncTrigger.RemovePassword
                && trigger != CloudSyncTrigger.SetPassword
                && trigger != CloudSyncTrigger.WipeData
    }
}