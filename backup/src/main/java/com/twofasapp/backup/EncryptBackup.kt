package com.twofasapp.backup

import com.twofasapp.backup.domain.BackupCipherEncryptResult
import com.twofasapp.backup.domain.BackupCipherPlainData
import com.twofasapp.backup.domain.KeyEncoded
import com.twofasapp.backup.domain.Password
import com.twofasapp.backup.domain.PlainData
import com.twofasapp.backup.domain.SaltEncoded
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.extensions.ifNotBlank
import io.reactivex.Scheduler
import io.reactivex.Single

class EncryptBackup(
    private val backupCipherService: BackupCipherService,
    private val analyticsService: com.twofasapp.core.analytics.AnalyticsService,
    private val jsonSerializer: com.twofasapp.serialization.JsonSerializer,
) : UseCaseParameterized<EncryptBackup.Params, Single<EncryptBackup.Result>> {

    data class Params(
        val backup: com.twofasapp.prefs.model.RemoteBackup,
        val password: String?,
        val saltEncoded: String?,
        val keyEncoded: String?,
    )

    sealed class Result {
        data class Success(
            val encryptedRemoteBackup: com.twofasapp.prefs.model.RemoteBackup,
            val saltEncoded: SaltEncoded?,
            val keyEncoded: KeyEncoded?,
        ) : Result()

        class Error(val throwable: Throwable) : Result()
    }

    override fun execute(params: Params, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<Result> {
        if (params.password.isNullOrBlank() && params.saltEncoded.isNullOrBlank() && params.keyEncoded.isNullOrBlank()) {
            return Single.just(Result.Success(params.backup, null, null))
        }

        return Single.create<Result> { emitter ->

            val result = backupCipherService.encrypt(
                data = BackupCipherPlainData(
                    reference = PlainData(BackupCipherService.REFERENCE),
                    services = PlainData(jsonSerializer.serialize(params.backup.services)),
                ),
                password = params.password.ifNotBlank { Password(it) },
                saltEncoded = params.saltEncoded.ifNotBlank { SaltEncoded(it) },
                keyEncoded = params.keyEncoded.ifNotBlank { KeyEncoded(it) },
            )

            when (result) {
                is BackupCipherEncryptResult.Success -> {
                    emitter.onSuccess(
                        Result.Success(
                            encryptedRemoteBackup = params.backup.copy(
                                services = emptyList(),
                                reference = result.data.reference.value,
                                servicesEncrypted = result.data.services.value,
                            ),
                            saltEncoded = result.saltEncoded,
                            keyEncoded = result.keyEncoded,
                        )
                    )
                }
                is BackupCipherEncryptResult.Failure -> {
                    emitter.onSuccess(Result.Error(result.reason))
                    analyticsService.captureException(result.reason)
                }
            }
        }.subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}