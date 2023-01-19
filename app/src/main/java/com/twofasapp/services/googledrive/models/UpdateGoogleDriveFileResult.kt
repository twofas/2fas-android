package com.twofasapp.services.googledrive.models

sealed class UpdateGoogleDriveFileResult {

    class Success : UpdateGoogleDriveFileResult()

    data class Failure(
        val type: GoogleDriveErrorType,
        val throwable: Throwable? = null,
    ) : UpdateGoogleDriveFileResult()
}