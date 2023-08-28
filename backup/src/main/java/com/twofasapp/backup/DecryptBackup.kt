package com.twofasapp.backup

import com.twofasapp.backup.domain.BackupCipherDecryptResult
import com.twofasapp.backup.domain.BackupCipherEncryptedData
import com.twofasapp.backup.domain.EncryptedData
import com.twofasapp.backup.domain.KeyEncoded
import com.twofasapp.backup.domain.Password
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.extensions.ifNotBlank
import com.twofasapp.prefs.model.isSet
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.crypto.AEADBadTagException

class DecryptBackup(
    private val backupCipherService: BackupCipherService,
    private val jsonSerializer: com.twofasapp.serialization.JsonSerializer,
) : UseCaseParameterized<DecryptBackup.Params, Single<DecryptBackup.Result>> {

    data class Params(
        val backup: com.twofasapp.prefs.model.RemoteBackup,
        val password: String?,
        val remoteBackupKey: com.twofasapp.prefs.model.RemoteBackupKey?,
    )

    sealed class Result {
        data class Success(
            val remoteBackup: com.twofasapp.prefs.model.RemoteBackup,
        ) : Result()

        data class NoPassword(val remoteBackup: com.twofasapp.prefs.model.RemoteBackup) : Result()
        object WrongPasswordError : Result()
        object UnknownError : Result()
    }

    override fun execute(params: Params, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<Result> {
        if (params.password.isNullOrBlank() && params.remoteBackupKey.isSet().not()) {
            if (params.backup.servicesEncrypted.isNullOrBlank().not()) {
                return Single.just(Result.NoPassword(params.backup))
            } else {
                return Single.just(Result.Success(params.backup))
            }
        }

        return Single.create<Result> { emitter ->
            val result = backupCipherService.decrypt(
                encryptedData = BackupCipherEncryptedData(
                    reference = EncryptedData(params.backup.reference.orEmpty()),
                    services = EncryptedData(params.backup.servicesEncrypted.orEmpty()),
                ),
                password = params.password.ifNotBlank { Password(it) },
                keyEncoded = params.remoteBackupKey?.keyEncoded.ifNotBlank { KeyEncoded(it) },
            )

            when (result) {
                is BackupCipherDecryptResult.Success -> {
                    emitter.onSuccess(
                        Result.Success(
                            remoteBackup = params.backup.copy(
                                services = jsonSerializer.deserialize(result.data.services.value),
                                servicesEncrypted = null,
                                reference = null
                            ),
                        )
                    )
                }

                is BackupCipherDecryptResult.Failure -> {
                    when (result.reason) {
                        is AEADBadTagException -> emitter.onSuccess(Result.WrongPasswordError)
                        else -> {
                            result.reason.printStackTrace()
                            emitter.onSuccess(Result.UnknownError)
                        }
                    }
                }
            }
        }.subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}