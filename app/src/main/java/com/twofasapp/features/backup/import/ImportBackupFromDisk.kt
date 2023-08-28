package com.twofasapp.features.backup.import

import android.content.Context
import android.net.Uri
import com.twofasapp.backup.DecryptBackup
import com.twofasapp.parsers.LegacyTypeToId
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.usecases.services.AddService
import com.twofasapp.usecases.services.GetServices
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.BufferedReader
import java.util.Locale

class ImportBackupFromDisk(
    private val context: Context,
    private val getServices: GetServices,
    private val addService: AddService,
    private val storeGroups: StoreGroups,
    private val decryptBackup: DecryptBackup,
    private val jsonSerializer: com.twofasapp.serialization.JsonSerializer,
) : ImportBackup {

    data class Content(
        val numberOfServices: Int,
        val isPasswordProtected: Boolean,
    )

    override fun read(fileUri: Uri): Maybe<Content> =
        readBackup(fileUri, null).flatMapMaybe {
            when (it) {
                is DecryptBackup.Result.Success -> {
                    Maybe.just(
                        Content(
                            numberOfServices = it.remoteBackup.services.size,
                            isPasswordProtected = it.remoteBackup.reference.isNullOrBlank().not(),
                        )
                    )
                }

                is DecryptBackup.Result.NoPassword -> {
                    Maybe.just(
                        Content(
                            numberOfServices = it.remoteBackup.services.size,
                            isPasswordProtected = it.remoteBackup.reference.isNullOrBlank().not(),
                        )
                    )
                }

                else -> {
                    Maybe.empty()
                }
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun import(fileUri: Uri, password: String?): Single<ImportBackup.Result> =
        Single.create<ImportBackup.Result> { emitter ->
            when (val result = readBackup(fileUri, password).blockingGet()) {
                is DecryptBackup.Result.Success -> {
                    val services = getServices.execute().blockingGet()
                    storeGroups.append(result.remoteBackup.groups.map { remoteGroup -> remoteGroup.toLocal() })

                    val localSecrets = services.map { it.secret.lowercase(Locale.US) }

                    result.remoteBackup.services
                        .filter { localSecrets.contains(it.secret.lowercase(Locale.US)).not() }
                        .sortedBy { it.order.position }
                        .map {
                            val serviceTypeIdFromLegacy = it.type?.name?.let { type -> LegacyTypeToId.serviceIds.getOrDefault(type, null) }
                            var iconCollectionIdFromLegacy = ServiceIcons.getIconCollection(serviceTypeIdFromLegacy.orEmpty())
                            val brandId = it.icon?.brand?.id?.name
                            if (brandId.isNullOrBlank().not()) {
                                val serviceTypeIdForBrand = LegacyTypeToId.serviceIds.getOrDefault(brandId!!, null)
                                if (serviceTypeIdForBrand.isNullOrBlank().not()) {
                                    iconCollectionIdFromLegacy = ServiceIcons.getIconCollection(serviceTypeIdForBrand!!)
                                }
                            }

                            if (iconCollectionIdFromLegacy.isBlank() || iconCollectionIdFromLegacy == ServiceIcons.defaultCollectionId) {
                                val serviceTypeIdForManualName = LegacyTypeToId.manualNames.getOrDefault(it.name.lowercase(), null)
                                if (serviceTypeIdForManualName.isNullOrBlank().not()) {
                                    iconCollectionIdFromLegacy = ServiceIcons.getIconCollection(serviceTypeIdForManualName!!)
                                }
                            }

                            ServiceDto.fromRemote(
                                remoteService = it,
                                serviceTypeIdFromLegacy = serviceTypeIdFromLegacy,
                                iconCollectionIdFromLegacy = iconCollectionIdFromLegacy
                            )
                        }
                        .forEach { addService.execute(AddService.Params(it, AddService.Trigger.ADD_FROM_BACKUP)).blockingGet() }

                    emitter.onSuccess(ImportBackup.Result.Success)
                }

                DecryptBackup.Result.WrongPasswordError -> emitter.onSuccess(ImportBackup.Result.WrongPasswordError)
                DecryptBackup.Result.UnknownError -> emitter.onSuccess(ImportBackup.Result.UnknownError)
                is DecryptBackup.Result.NoPassword -> emitter.onSuccess(ImportBackup.Result.WrongPasswordError)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    private fun readBackup(fileUri: Uri, password: String?): Single<DecryptBackup.Result> =
        Single.fromCallable {
            val size = context.contentResolver.openAssetFileDescriptor(fileUri, "r")?.length
                ?: 0

            if (size > 10 * 1024 * 1024) {
                throw FileTooBigException()
            }

            val inputStream = context.contentResolver.openInputStream(fileUri)!!
            val content = inputStream.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText)
            jsonSerializer.deserialize<com.twofasapp.prefs.model.RemoteBackup>(content)

        }
            .flatMap {
                decryptBackup.execute(
                    DecryptBackup.Params(
                        backup = it,
                        password = password,
                        remoteBackupKey = null
                    )
                )
            }
            .doOnError {
                Timber.e(it.printStackTrace().toString())
            }
}