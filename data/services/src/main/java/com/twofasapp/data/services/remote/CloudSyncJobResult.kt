package com.twofasapp.data.services.remote

import com.twofasapp.data.services.domain.CloudSyncTrigger

sealed interface CloudSyncJobResult {
    data object Success : CloudSyncJobResult
    data class Failure(val trigger: CloudSyncTrigger) : CloudSyncJobResult
}