package com.twofasapp.services.backup.usecases

import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.prefs.model.RemoteBackup
import com.twofasapp.prefs.usecase.RemoteBackupKeyPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.services.backup.models.GetRemoteBackupResult
import com.twofasapp.services.backup.models.GetRemoteBackupResult.Failure
import com.twofasapp.services.backup.models.GetRemoteBackupResult.Success
import com.twofasapp.services.backup.models.RemoteBackupErrorType
import com.twofasapp.backup.DecryptBackup
import com.twofasapp.services.googledrive.GoogleDriveService
import com.twofasapp.services.googledrive.models.GetGoogleDriveFileResult
import com.twofasapp.services.googledrive.models.mapToRemoteBackupErrorType
import io.reactivex.Scheduler
import io.reactivex.Single

class GetRemoteBackup(
    private val googleDriveService: GoogleDriveService,
    private val jsonSerializer: com.twofasapp.serialization.JsonSerializer,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val remoteBackupKeyPreference: RemoteBackupKeyPreference,
    private val decryptBackup: DecryptBackup,
) : UseCaseParameterized<GetRemoteBackup.Params, Single<GetRemoteBackupResult>> {

    data class Params(
        val password: String? = null
    )

    override fun execute(params: Params, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<GetRemoteBackupResult> {
        return googleDriveService.getBackupFile()
            .flatMap { mapResult(it, params) }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }

    private fun mapResult(result: GetGoogleDriveFileResult, params: Params): Single<GetRemoteBackupResult> {
        return Single.fromCallable {
            when (result) {
                is GetGoogleDriveFileResult.Success -> mapSuccess(result, params)
                is GetGoogleDriveFileResult.Failure -> mapFailure(result)
            }
        }
    }

    private fun mapSuccess(result: GetGoogleDriveFileResult.Success, params: Params): GetRemoteBackupResult =
        if (result.file.content.isBlank()) {
            Success(RemoteBackup.createEmpty())
        } else {
            try {
                val remoteBackup = jsonSerializer.deserialize<RemoteBackup>(result.file.content)
                val isBackupPasswordProtected = remoteBackup.reference.isNullOrBlank().not()
                remoteBackupStatusPreference.put { it.copy(reference = remoteBackup.reference) }

                if (isBackupPasswordProtected) {
                    val decryptResult = decryptBackup.execute(
                        DecryptBackup.Params(
                            backup = remoteBackup,
                            password = params.password,
                            remoteBackupKey = remoteBackupKeyPreference.get()
                        )
                    ).blockingGet()

                    when (decryptResult) {
                        is DecryptBackup.Result.Success -> Success(decryptResult.remoteBackup)
                        is DecryptBackup.Result.NoPassword -> Failure(RemoteBackupErrorType.DECRYPT_NO_PASSWORD)
                        DecryptBackup.Result.WrongPasswordError -> Failure(RemoteBackupErrorType.DECRYPT_WRONG_PASSWORD)
                        DecryptBackup.Result.UnknownError -> Failure(RemoteBackupErrorType.DECRYPT_UNKNOWN_FAILURE)
                    }

                } else {
                    remoteBackupKeyPreference.delete()
                    Success(remoteBackup)
                }
            } catch (e: Exception) {
                Failure(RemoteBackupErrorType.JSON_PARSING_FAILURE, e)
            }
        }

    private fun mapFailure(result: GetGoogleDriveFileResult.Failure): GetRemoteBackupResult =
        Failure(
            type = result.type.mapToRemoteBackupErrorType(),
            throwable = result.throwable,
        )

}