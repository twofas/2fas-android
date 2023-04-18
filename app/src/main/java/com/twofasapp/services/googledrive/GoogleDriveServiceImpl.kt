package com.twofasapp.services.googledrive

import android.content.Context
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
import com.twofasapp.services.googleauth.models.AccountCredentials
import com.twofasapp.services.googleauth.usecases.GetAccountCredentials
import com.twofasapp.services.googledrive.models.DeleteGoogleDriveFileResult
import com.twofasapp.services.googledrive.models.GetGoogleDriveFileResult
import com.twofasapp.services.googledrive.models.GoogleDriveErrorType
import com.twofasapp.services.googledrive.models.GoogleDriveErrorType.CREDENTIALS_NOT_FOUND
import com.twofasapp.services.googledrive.models.GoogleDriveFile
import com.twofasapp.services.googledrive.models.UpdateGoogleDriveFileResult
import io.reactivex.Single
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Collections

class GoogleDriveServiceImpl(
    private val context: Context,
    private val getAccountCredentials: GetAccountCredentials,
) : GoogleDriveService {

    companion object {
        private const val APP_NAME = "2FAS"
        private val backupVersions = listOf(
            "2fas-backup-v3.json",
            "2fas-backup-v2.json",
            "2fas-backup.json"
        )
    }

    override fun getBackupFile(): Single<GetGoogleDriveFileResult> =
        Single.create { emitter ->
            try {
                Timber.d("GetFile -> Starting...")

                val credentials = getAccountCredentials()
                if (credentials == null) {
                    emitter.onSuccess(GetGoogleDriveFileResult.Failure(type = CREDENTIALS_NOT_FOUND))
                    return@create
                }

                val drive = getDrive(credentials)
                val backupFileId = getFiles(drive)
                    ?.filter { backupVersions.contains(it.name) }
                    ?.firstOrNull()
                    ?.id

                if (backupFileId != null) {
                    drive.files()[backupFileId]?.executeMediaAsInputStream()?.use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            val file = GoogleDriveFile(
                                id = backupFileId,
                                content = reader.readText()
                            )

                            Timber.d("GetFile <- ${file.content}")
                            Timber.d("GetFile <- Success")
                            emitter.onSuccess(GetGoogleDriveFileResult.Success(file))
                        }
                    }
                } else {
                    Timber.d("GetFile <- Success")
                    emitter.onSuccess(
                        GetGoogleDriveFileResult.Failure(
                            type = GoogleDriveErrorType.FILE_NOT_FOUND
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.d("GetFile <- Error: $e")
                e.printStackTrace()
                emitter.onSuccess(
                    GetGoogleDriveFileResult.Failure(
                        type = mapExceptionToErrorType(e),
                        throwable = e
                    )
                )
            }
        }

    override fun updateBackupFile(content: String): Single<UpdateGoogleDriveFileResult> {
        return Single.create { emitter ->
            try {
                Timber.d("UpdateFile -> Starting...")

                val credentials = getAccountCredentials()
                if (credentials == null) {
                    emitter.onSuccess(UpdateGoogleDriveFileResult.Failure(type = CREDENTIALS_NOT_FOUND))
                    return@create
                }

                val drive = getDrive(credentials)
                val backupFileId = getFiles(drive)?.firstOrNull { it.name == backupVersions.first() }?.id

                if (backupFileId.isNullOrBlank().not()) {
                    drive.files().update(
                        backupFileId,
                        File().setName(backupVersions.first()),
                        ByteArrayContent.fromString("text/plain", content)
                    ).execute()
                    Timber.d("UpdateFile <- $content")
                    Timber.d("UpdateFile <- Success")
                    emitter.onSuccess(UpdateGoogleDriveFileResult.Success())
                } else {
                    val metadata = File()
                        .setParents(listOf("appDataFolder"))
                        .setMimeType("application/json")
                        .setName(backupVersions.first())
                    val contentStream = ByteArrayContent.fromString("text/plain", content)

                    drive.files().create(metadata, contentStream)?.execute()

                    Timber.d("CreateFile <- $content")
                    Timber.d("CreateFile <- Success")

                    emitter.onSuccess(UpdateGoogleDriveFileResult.Success())
                }
            } catch (e: Exception) {
                Timber.d("UpdateFile <- Error: $e")
                emitter.onSuccess(
                    UpdateGoogleDriveFileResult.Failure(
                        type = mapExceptionToErrorType(e),
                        throwable = e
                    )
                )
            }
        }
    }

    override fun deleteBackupFile(): Single<DeleteGoogleDriveFileResult> {
        return Single.create { emitter ->
            try {
                val credentials = getAccountCredentials()
                if (credentials == null) {
                    emitter.onSuccess(DeleteGoogleDriveFileResult.Failure(type = CREDENTIALS_NOT_FOUND))
                    return@create
                }

                val drive = getDrive(credentials)
                val backupFileId = getFiles(drive)?.firstOrNull { it.name == backupVersions.first() }?.id
                if (backupFileId.isNullOrBlank().not()) {
                    drive.files().delete(backupFileId).execute()
                    emitter.onSuccess(DeleteGoogleDriveFileResult.Success())
                }
            } catch (e: Exception) {
                Timber.d("UpdateFile <- Error: $e")
                emitter.onSuccess(
                    DeleteGoogleDriveFileResult.Failure(
                        type = mapExceptionToErrorType(e),
                        throwable = e
                    )
                )
            }
        }
    }

    private fun mapExceptionToErrorType(e: Exception): GoogleDriveErrorType {
        return when (e) {
            is GoogleJsonResponseException -> GoogleDriveErrorType.HTTP_API_FAILURE
            is GooglePlayServicesAvailabilityException -> GoogleDriveErrorType.PLAY_SERVICES_UNAVAILABLE
            is UserRecoverableAuthException -> GoogleDriveErrorType.USER_PERMISSION_DENIED
            is UserRecoverableAuthIOException -> GoogleDriveErrorType.USER_PERMISSION_DENIED
            is GoogleAuthException -> GoogleDriveErrorType.AUTH_FAILURE
            is IOException -> GoogleDriveErrorType.NETWORK_UNAVAILABLE
            else -> GoogleDriveErrorType.UNKNOWN
        }
    }

    private fun getFiles(drive: Drive) =
        drive.files().list()
            .setSpaces("appDataFolder")
            .execute()
            ?.files
            ?.sortedByDescending { it.name.replace("2fas-backup-v", "").replace(".json", "").toIntOrNull() }

    private fun getAccountCredentials() = getAccountCredentials.execute()

    private fun getDrive(accountCredentials: AccountCredentials): Drive {
        val googleAccountCredential = GoogleAccountCredential
            .usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_APPDATA))
            .apply { selectedAccount = accountCredentials.account }

        return Drive.Builder(
            NetHttpTransport.Builder().build(),
            GsonFactory(),
            googleAccountCredential
        )
            .setApplicationName(APP_NAME)
            .build()
    }
}