package com.twofasapp.services.backup.usecases

import com.twofasapp.base.usecase.UseCase
import com.twofasapp.services.backup.models.DeleteRemoteBackupResult
import com.twofasapp.services.googledrive.GoogleDriveService
import com.twofasapp.services.googledrive.models.DeleteGoogleDriveFileResult
import com.twofasapp.services.googledrive.models.mapToRemoteBackupErrorType
import io.reactivex.Scheduler
import io.reactivex.Single

class DeleteRemoteBackup(
    private val googleDriveService: GoogleDriveService,
) : UseCase<Single<DeleteRemoteBackupResult>> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<DeleteRemoteBackupResult> =
        googleDriveService.deleteBackupFile().map {
            when (it) {
                is DeleteGoogleDriveFileResult.Success -> DeleteRemoteBackupResult.Success()
                is DeleteGoogleDriveFileResult.Failure -> DeleteRemoteBackupResult.Failure(
                    type = it.type.mapToRemoteBackupErrorType(),
                    throwable = it.throwable,
                )
            }
        }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
}