package com.twofasapp.backup.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.backup.EncryptBackup
import com.twofasapp.backup.ui.export.ExportBackup
import com.twofasapp.environment.AppConfig
import com.twofasapp.prefs.usecase.ServicesOrderPreference
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.services.domain.GetServicesUseCase
import com.twofasapp.time.domain.TimeProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ExportBackupToDisk(
    private val context: Context,
    private val appConfig: AppConfig,
    private val getServices: GetServicesUseCase,
    private val timeProvider: TimeProvider,
    private val servicesOrderPreference: ServicesOrderPreference,
    private val storeGroups: StoreGroups,
    private val encryptBackup: EncryptBackup,
    private val jsonSerializer: com.twofasapp.serialization.JsonSerializer,
) : ExportBackup {

    override fun execute(fileUri: Uri?, password: String?): Single<ExportBackup.Result> {
        return getServices.execute()
            .map {
                val servicesOrder = servicesOrderPreference.get()
                com.twofasapp.prefs.model.RemoteBackup(
                    updatedAt = timeProvider.systemCurrentTime(),
                    services = it
                        .map {
                            it.mapToRemote()
                                .copy(order = com.twofasapp.prefs.model.RemoteService.Order(position = servicesOrder.ids.indexOf(it.id)))
                        },
                    appVersionCode = appConfig.versionCode,
                    appVersionName = appConfig.versionName,
                    groups = storeGroups.all().list.filter { group -> group.id != null }.map { group -> group.toRemote() },
                    account = null,
                )
            }
            .flatMap { encryptBackup.execute(EncryptBackup.Params(backup = it, password = password, saltEncoded = null, keyEncoded = null)) }
            .flatMap { result ->
                when (result) {
                    is EncryptBackup.Result.Success -> {
                        // Case when we just want to backup content without saving it to disk
                        if (fileUri != null) {
                            Completable.fromCallable {
                                val outputStream = context.contentResolver.openOutputStream(fileUri)!!
                                outputStream.use {
                                    it.write(
                                        jsonSerializer.serializePretty(result.encryptedRemoteBackup).toByteArray(Charsets.UTF_8)
                                    )
                                }
                            }.toSingle { ExportBackup.Result.Success(jsonSerializer.serializePretty(result.encryptedRemoteBackup)) }
                        } else {
                            Single.just(ExportBackup.Result.Success(jsonSerializer.serializePretty(result.encryptedRemoteBackup)))
                        }
                    }
                    is EncryptBackup.Result.Error -> Single.just(ExportBackup.Result.UnknownError)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}