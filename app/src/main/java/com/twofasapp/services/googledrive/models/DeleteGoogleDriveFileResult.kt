package com.twofasapp.services.googledrive.models

sealed class DeleteGoogleDriveFileResult {

    class Success : DeleteGoogleDriveFileResult()

    data class Failure(
        val type: GoogleDriveErrorType,
        val throwable: Throwable? = null,
    ) : DeleteGoogleDriveFileResult()
}