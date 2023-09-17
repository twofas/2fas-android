package com.twofasapp.services.googledrive

import com.twofasapp.services.googledrive.models.GetGoogleDriveFileResult
import com.twofasapp.services.googledrive.models.UpdateGoogleDriveFileResult
import io.reactivex.Single

interface GoogleDriveService {
    fun getBackupFile(): Single<GetGoogleDriveFileResult>
    fun updateBackupFile(content: String): Single<UpdateGoogleDriveFileResult>
}