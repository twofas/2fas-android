package com.twofasapp.services.googledrive.models

sealed class GetGoogleDriveFileResult {
    data class Success(
        val file: GoogleDriveFile
    ) : GetGoogleDriveFileResult()

    data class Failure(
        val type: GoogleDriveErrorType,
        val throwable: Throwable? = null
    ) : GetGoogleDriveFileResult()
}