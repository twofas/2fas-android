package com.twofasapp.data.cloud.googledrive

interface GoogleDrive {
    suspend fun getBackupFile(): GoogleDriveFileResult
    suspend fun updateBackupFile(backupContent: String): GoogleDriveResult
    suspend fun deleteBackupFile(): GoogleDriveResult
}