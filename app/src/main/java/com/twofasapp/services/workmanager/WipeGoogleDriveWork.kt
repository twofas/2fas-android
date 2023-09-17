package com.twofasapp.services.workmanager

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Single
import org.koin.core.component.KoinComponent

class WipeGoogleDriveWork(
    appContext: Context,
    workerParams: WorkerParameters
) : RxWorker(appContext, workerParams), KoinComponent {

    //    private val revokeAccessToGoogle: RevokeAccessToGoogle by inject()
//    private val deleteRemoteBackup: DeleteRemoteBackup by inject()

    override fun createWork(): Single<Result> = Single.just(Result.success())
//        deleteRemoteBackup.execute()
//            .flatMap { revokeAccessToGoogle.execute() }
//            .map { Result.success() }
}