package com.twofasapp.features.backup.import

import android.net.Uri
import io.reactivex.Maybe
import io.reactivex.Single

interface ImportBackup {
    fun read(fileUri: Uri): Maybe<ImportBackupFromDisk.Content>
    fun import(fileUri: Uri, password: String?): Single<Result>

    sealed class Result {
        object Success : Result()
        object WrongPasswordError : Result()
        object UnknownError : Result()
    }
}