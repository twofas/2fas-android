package com.twofasapp.backup.ui.export

import android.net.Uri
import io.reactivex.Single

interface ExportBackup {
    fun execute(fileUri: Uri?, password: String? = null): Single<Result>

    sealed class Result {
        data class Success(val backupContent: String) : Result()
        object UnknownError : Result()
    }
}