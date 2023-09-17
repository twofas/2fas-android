package com.twofasapp.data.cloud.googledrive

sealed interface GoogleDriveFileResult {
    data class Success(
        val fileContent: String
    ) : GoogleDriveFileResult

    data class Failure(
        val error: GoogleDriveError,
        val throwable: Throwable? = null,
    ) : GoogleDriveFileResult
}