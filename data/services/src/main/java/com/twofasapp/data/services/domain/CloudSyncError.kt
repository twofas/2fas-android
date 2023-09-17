package com.twofasapp.data.services.domain

import com.twofasapp.data.cloud.googledrive.GoogleDriveError

enum class CloudSyncError(val code: Int) {
    Unknown(1),
    NetworkUnavailable(2),
    GoogleUserPermissionDenied(3),
    GooglePlayServicesUnavailable(4),
    GoogleAuthFailure(5),
    JsonParsingFailure(6),
    SyncFailure(7),
    HttpApiFailure(8),
    FileNotFound(9),
    CredentialsNotFound(10),
    EncryptUnknownFailure(11),
    DecryptNoPassword(12),
    DecryptWrongPassword(13),
    DecryptUnknownFailure(14),
}

internal fun GoogleDriveError.asDomain(): CloudSyncError {
    return when (this) {
        GoogleDriveError.NetworkUnavailable -> CloudSyncError.NetworkUnavailable
        GoogleDriveError.UserPermissionDenied -> CloudSyncError.GoogleUserPermissionDenied
        GoogleDriveError.PlayServicesUnavailable -> CloudSyncError.GooglePlayServicesUnavailable
        GoogleDriveError.AuthFailure -> CloudSyncError.GoogleAuthFailure
        GoogleDriveError.HttpApiFailure -> CloudSyncError.HttpApiFailure
        GoogleDriveError.FileNotFound -> CloudSyncError.FileNotFound
        GoogleDriveError.CredentialsNotFound -> CloudSyncError.CredentialsNotFound
        GoogleDriveError.Unknown -> CloudSyncError.Unknown

    }
}