package com.twofasapp.data.services.remote

import com.twofasapp.data.services.domain.CloudSyncTrigger

sealed interface CloudSyncResult {
    data object Success : CloudSyncResult
    data class Failure(val trigger: CloudSyncTrigger) : CloudSyncResult
}