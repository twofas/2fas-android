package com.twofasapp.services.backup.usecases

import com.twofasapp.services.backup.models.RemoteBackupErrorType
import com.twofasapp.services.backup.models.UpdateRemoteBackupResult
import com.twofasapp.backup.EncryptBackup
import com.twofasapp.services.googledrive.GoogleDriveService
import com.twofasapp.services.googledrive.models.UpdateGoogleDriveFileResult
import com.twofasapp.services.googledrive.models.mapToRemoteBackupErrorType
import com.twofasapp.prefs.model.isSet
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.environment.AppConfig
import com.twofasapp.usecases.services.GetServices
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.services.domain.StoreServicesOrder
import io.reactivex.Scheduler
import io.reactivex.Single

class UpdateRemoteBackup(
    private val googleDriveService: GoogleDriveService,
    private val getServices: GetServices,
    private val storeServicesOrder: StoreServicesOrder,
    private val storeGroups: StoreGroups,
    private val remoteBackupStatusPreference: com.twofasapp.prefs.usecase.RemoteBackupStatusPreference,
    private val remoteBackupKeyPreference: com.twofasapp.prefs.usecase.RemoteBackupKeyPreference,
    private val jsonSerializer: com.twofasapp.serialization.JsonSerializer,
    private val encryptBackup: EncryptBackup,
    private val appConfig: AppConfig,
) : UseCaseParameterized<UpdateRemoteBackup.Params, Single<UpdateRemoteBackupResult>> {

    data class Params(
        val isFirstConnect: Boolean,
        val updatedAt: Long,
        val password: String?,
        val remoteBackupKey: com.twofasapp.prefs.model.RemoteBackupKey?,
    )

    override fun execute(params: Params, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<UpdateRemoteBackupResult> {
        return getServices.execute()
            .map { services ->
                val servicesOrder = storeServicesOrder.getOrder()
                val groups = storeGroups.all().list
                val servicesOrdered = services.map {
                    val orderPosition = com.twofasapp.prefs.model.RemoteService.Order(position = servicesOrder.ids.indexOf(it.id))
                    if (params.isFirstConnect) {
                        it.mapToRemote().copy(order = orderPosition, updatedAt = params.updatedAt)
                    } else {
                        it.mapToRemote().copy(order = orderPosition)
                    }
                }

                com.twofasapp.prefs.model.RemoteBackup(
                    updatedAt = params.updatedAt,
                    appVersionCode = appConfig.versionCode,
                    appVersionName = appConfig.versionName,
                    groups = groups.filter { it.id != null }.map { it.toRemote() },
                    services = servicesOrdered,
                    account = remoteBackupStatusPreference.get().account,
                )
            }
            .flatMap { newBackup ->
                // Encrypt backup file if necessary
                if (params.password.isNullOrBlank().not() || params.remoteBackupKey.isSet()) {
                    encryptBackup.execute(
                        EncryptBackup.Params(
                            backup = newBackup,
                            password = params.password,
                            saltEncoded = params.remoteBackupKey?.saltEncoded,
                            keyEncoded = params.remoteBackupKey?.keyEncoded,
                        )
                    )
                        .map { mapEncryptionResult(it) }
                        .subscribeOn(subscribeScheduler)
                } else {
                    Single.just(EncryptBackup.Result.Success(newBackup, null, null))
                        .doOnSuccess {
                            remoteBackupStatusPreference.put { it.copy(reference = null) }
                            remoteBackupKeyPreference.delete()
                        }
                }
            }
            .flatMap { encryptionResult ->
                when (encryptionResult) {
                    is EncryptBackup.Result.Success -> {
                        googleDriveService.updateBackupFile(jsonSerializer.serialize(data = encryptionResult.encryptedRemoteBackup))
                            .map {
                                when (it) {
                                    is UpdateGoogleDriveFileResult.Success -> {
                                        UpdateRemoteBackupResult.Success()
                                    }
                                    is UpdateGoogleDriveFileResult.Failure -> {
                                        UpdateRemoteBackupResult.Failure(
                                            type = it.type.mapToRemoteBackupErrorType(),
                                            throwable = it.throwable
                                        )
                                    }
                                }
                            }
                            .subscribeOn(subscribeScheduler)
                    }
                    is EncryptBackup.Result.Error -> Single.just(
                        UpdateRemoteBackupResult.Failure(
                            RemoteBackupErrorType.ENCRYPT_UNKNOWN_FAILURE,
                            null
                        )
                    )
                }
            }

            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }

    private fun mapEncryptionResult(result: EncryptBackup.Result): EncryptBackup.Result {
        if (result is EncryptBackup.Result.Success) {
            remoteBackupKeyPreference.put {
                it.copy(
                    saltEncoded = result.saltEncoded?.value.orEmpty(),
                    keyEncoded = result.keyEncoded?.value.orEmpty(),
                )
            }
            remoteBackupStatusPreference.put { it.copy(reference = result.encryptedRemoteBackup.reference) }
        }

        return result
    }
}