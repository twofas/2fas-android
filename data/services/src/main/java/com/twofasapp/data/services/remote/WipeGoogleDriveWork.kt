package com.twofasapp.data.services.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.data.cloud.googleauth.GoogleAuth
import com.twofasapp.data.cloud.googledrive.GoogleDrive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WipeGoogleDriveWork(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val googleDrive: GoogleDrive by inject()
    private val googleAuth: GoogleAuth by inject()

    override suspend fun doWork(): Result {
        googleDrive.deleteBackupFile()
        googleAuth.revokeAccess()

        return Result.success()
    }
}