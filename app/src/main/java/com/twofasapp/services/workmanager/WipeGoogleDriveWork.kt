package com.twofasapp.services.workmanager

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.twofasapp.services.backup.usecases.DeleteRemoteBackup
import com.twofasapp.services.googleauth.usecases.RevokeAccessToGoogle
import io.reactivex.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WipeGoogleDriveWork(
    appContext: Context,
    workerParams: WorkerParameters
) : RxWorker(appContext, workerParams), KoinComponent {

    private val revokeAccessToGoogle: RevokeAccessToGoogle by inject()
    private val deleteRemoteBackup: DeleteRemoteBackup by inject()

    override fun createWork(): Single<Result> =
        deleteRemoteBackup.execute()
            .flatMap { revokeAccessToGoogle.execute() }
            .map { Result.success() }
}