package com.twofasapp.data.cloud.googledrive

import android.app.Application
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.cloud.googleauth.AccountCredentials
import com.twofasapp.data.cloud.googleauth.GoogleAuth
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Collections

internal class GoogleDriveImpl(
    private val dispatchers: Dispatchers,
    private val context: Application,
    private val googleAuth: GoogleAuth,
) : GoogleDrive {

    companion object {
        private val backupVersions = listOf(
            "2fas-backup-v3.json",
            "2fas-backup-v2.json",
            "2fas-backup.json"
        )
    }

    override suspend fun getBackupFile(): GoogleDriveFileResult {
        return withContext(dispatchers.io) {
            try {
                Timber.d("GetFile -> Starting...")

                val credentials = googleAuth.accountCredentials()
                    ?: return@withContext GoogleDriveFileResult.Failure(error = GoogleDriveError.CredentialsNotFound)

                val drive = getDrive(credentials)
                val backupFileId = getFiles(drive)
                    ?.filter { backupVersions.contains(it.name) }
                    ?.firstOrNull()
                    ?.id

                if (backupFileId != null) {
                    val fileContent = drive.files()[backupFileId]?.executeMediaAsInputStream()?.use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            reader.readText()
                        }
                    }
                    Timber.d("GetFile <- \"$fileContent\"")
                    Timber.d("GetFile <- Success")

                    GoogleDriveFileResult.Success(fileContent.orEmpty())
                } else {
                    Timber.d("GetFile <- Success")
                    GoogleDriveFileResult.Failure(error = GoogleDriveError.FileNotFound)
                }

            } catch (e: Exception) {
                Timber.d("GetFile <- Error: $e")
                e.printStackTrace()
                GoogleDriveFileResult.Failure(error = mapExceptionToErrorType(e))
            }
        }
    }

    override suspend fun updateBackupFile(backupContent: String): GoogleDriveResult {
        return withContext(dispatchers.io) {
            try {
                Timber.d("UpdateFile -> Starting...")
                val credentials = googleAuth.accountCredentials()
                    ?: return@withContext GoogleDriveResult.Failure(error = GoogleDriveError.CredentialsNotFound)

                val drive = getDrive(credentials)
                val backupFileId = getFiles(drive)?.firstOrNull { it.name == backupVersions.first() }?.id

                if (backupFileId.isNullOrBlank().not()) {
                    drive.files().update(
                        backupFileId,
                        File().setName(backupVersions.first()),
                        ByteArrayContent.fromString("text/plain", backupContent)
                    ).execute()
                    Timber.d("UpdateFile <- $backupContent")
                    Timber.d("UpdateFile <- Success")
                    GoogleDriveResult.Success
                } else {
                    val metadata = File()
                        .setParents(listOf("appDataFolder"))
                        .setMimeType("application/json")
                        .setName(backupVersions.first())
                    val contentStream = ByteArrayContent.fromString("text/plain", backupContent)

                    drive.files().create(metadata, contentStream)?.execute()

                    Timber.d("CreateFile <- \"$backupContent\"")
                    Timber.d("CreateFile <- Success")

                    GoogleDriveResult.Success
                }

            } catch (e: Exception) {
                Timber.d("UpdateFile <- Error: $e")
                GoogleDriveResult.Failure(
                    error = mapExceptionToErrorType(e),
                    throwable = e
                )
            }
        }
    }

    override suspend fun deleteBackupFile(): GoogleDriveResult {
        return withContext(dispatchers.io) {
            try {
                Timber.d("DeleteFile -> Starting...")

                val credentials = googleAuth.accountCredentials()
                    ?: return@withContext GoogleDriveResult.Failure(error = GoogleDriveError.CredentialsNotFound)

                val drive = getDrive(credentials)
                val backupFileId = getFiles(drive)?.firstOrNull { it.name == backupVersions.first() }?.id
                if (backupFileId.isNullOrBlank().not()) {
                    drive.files().delete(backupFileId).execute()
                }

                Timber.d("DeleteFile <- Success")

                GoogleDriveResult.Success
            } catch (e: Exception) {
                Timber.d("DeleteFile <- Error: $e")
                GoogleDriveResult.Failure(error = mapExceptionToErrorType(e))
            }
        }
    }

    private fun mapExceptionToErrorType(e: Exception): GoogleDriveError {
        return when (e) {
            is GoogleJsonResponseException -> GoogleDriveError.HttpApiFailure
            is GooglePlayServicesAvailabilityException -> GoogleDriveError.PlayServicesUnavailable
            is UserRecoverableAuthException -> GoogleDriveError.UserPermissionDenied
            is UserRecoverableAuthIOException -> GoogleDriveError.UserPermissionDenied
            is GoogleAuthException -> GoogleDriveError.AuthFailure
            is IOException -> GoogleDriveError.NetworkUnavailable
            else -> GoogleDriveError.Unknown
        }
    }

    private fun getFiles(drive: Drive) =
        drive.files().list()
            .setSpaces("appDataFolder")
            .execute()
            ?.files
            ?.sortedByDescending { it.name.replace("2fas-backup-v", "").replace(".json", "").toIntOrNull() }

    private fun getDrive(accountCredentials: AccountCredentials): Drive {
        val googleAccountCredential = GoogleAccountCredential
            .usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_APPDATA))
            .apply { selectedAccount = accountCredentials.account }

        return Drive.Builder(
            NetHttpTransport.Builder().build(),
            GsonFactory(),
            googleAccountCredential
        )
            .setApplicationName("2FAS")
            .build()
    }
}