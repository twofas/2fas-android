package com.twofasapp.services.googledrive.models

import com.twofasapp.services.backup.models.RemoteBackupErrorType

enum class GoogleDriveErrorType {
    NETWORK_UNAVAILABLE,
    USER_PERMISSION_DENIED,
    PLAY_SERVICES_UNAVAILABLE,
    AUTH_FAILURE,
    HTTP_API_FAILURE,
    FILE_NOT_FOUND,
    CREDENTIALS_NOT_FOUND,
    UNKNOWN,
}

fun GoogleDriveErrorType.mapToRemoteBackupErrorType(): RemoteBackupErrorType {
    return when (this) {
        GoogleDriveErrorType.NETWORK_UNAVAILABLE -> RemoteBackupErrorType.NETWORK_UNAVAILABLE
        GoogleDriveErrorType.USER_PERMISSION_DENIED -> RemoteBackupErrorType.GOOGLE_USER_PERMISSION_DENIED
        GoogleDriveErrorType.PLAY_SERVICES_UNAVAILABLE -> RemoteBackupErrorType.GOOGLE_PLAY_SERVICES_UNAVAILABLE
        GoogleDriveErrorType.AUTH_FAILURE -> RemoteBackupErrorType.GOOGLE_AUTH_FAILURE
        GoogleDriveErrorType.HTTP_API_FAILURE -> RemoteBackupErrorType.HTTP_API_FAILURE
        GoogleDriveErrorType.FILE_NOT_FOUND -> RemoteBackupErrorType.FILE_NOT_FOUND
        GoogleDriveErrorType.CREDENTIALS_NOT_FOUND -> RemoteBackupErrorType.CREDENTIALS_NOT_FOUND
        GoogleDriveErrorType.UNKNOWN -> RemoteBackupErrorType.UNKNOWN
    }
}
