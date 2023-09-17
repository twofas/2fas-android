package com.twofasapp.data.cloud.googledrive

sealed interface GoogleDriveResult {
    data object Success : GoogleDriveResult
    data class Failure(
        val error: GoogleDriveError,
        val throwable: Throwable? = null,
    ) : GoogleDriveResult
}