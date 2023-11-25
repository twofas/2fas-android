package com.twofasapp.data.services

import android.app.Application
import android.net.Uri
import com.twofasapp.cipher.backup.BackupCipher
import com.twofasapp.cipher.backup.DataEncrypted
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.cloud.googledrive.GoogleDrive
import com.twofasapp.data.cloud.googledrive.GoogleDriveFileResult
import com.twofasapp.data.cloud.googledrive.GoogleDriveResult
import com.twofasapp.data.services.domain.BackupContent
import com.twofasapp.data.services.domain.BackupContentCreateResult
import com.twofasapp.data.services.domain.CloudBackupGetResult
import com.twofasapp.data.services.domain.CloudBackupStatus
import com.twofasapp.data.services.domain.CloudBackupUpdateResult
import com.twofasapp.data.services.domain.CloudSyncError
import com.twofasapp.data.services.domain.CloudSyncStatus
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.domain.asDomain
import com.twofasapp.data.services.exceptions.DecryptWrongPassword
import com.twofasapp.data.services.exceptions.FileTooBigException
import com.twofasapp.data.services.mapper.asBackup
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.data.services.remote.CloudSyncWorkDispatcher
import com.twofasapp.data.services.remote.WipeGoogleDriveWorkDispatcher
import com.twofasapp.parsers.LegacyTypeToId
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.model.RemoteBackupKey
import com.twofasapp.prefs.model.RemoteBackupStatusEntity
import com.twofasapp.prefs.model.isSet
import com.twofasapp.prefs.usecase.RemoteBackupKeyPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import javax.crypto.AEADBadTagException

class BackupRepositoryImpl(
    private val context: Application,
    private val dispatchers: Dispatchers,
    private val appBuild: AppBuild,
    private val timeProvider: TimeProvider,
    private val json: Json,
    private val servicesRepository: ServicesRepository,
    private val groupsRepository: GroupsRepository,
    private val backupCipher: BackupCipher,
    private val cloudSyncWorkDispatcher: CloudSyncWorkDispatcher,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val remoteBackupKeyPreference: RemoteBackupKeyPreference,
    private val wipeGoogleDriveWorkDispatcher: WipeGoogleDriveWorkDispatcher,
    private val googleDrive: GoogleDrive,
) : BackupRepository {

    private val cloudSyncStatusFlow = MutableStateFlow<CloudSyncStatus>(CloudSyncStatus.Default)
    private val passwordForCloudSync = MutableStateFlow<String?>(null)

    override fun dispatchCloudSync(trigger: CloudSyncTrigger, password: String?) {
        cloudSyncWorkDispatcher.tryDispatch(
            trigger = trigger,
            password = password,
        )
    }

    override fun dispatchWipeData() {
        GlobalScope.launch {
            remoteBackupStatusPreference.put(RemoteBackupStatusEntity(schemaVersion = BackupContent.CurrentSchema))
            remoteBackupStatusPreference.delete()
            remoteBackupKeyPreference.delete()
            wipeGoogleDriveWorkDispatcher.dispatch()
        }
    }

    override suspend fun createBackupContent(
        password: String?,
        keyEncoded: String?,
        saltEncoded: String?,
        account: String?
    ): BackupContentCreateResult {
        return withContext(dispatchers.io) {
            val groups = groupsRepository.observeGroups().first().asBackup()
            val services = servicesRepository.getServices().asBackup()

            val backupContent = BackupContent(
                services = services,
                groups = groups,
                updatedAt = timeProvider.systemCurrentTime(),
                appVersionCode = appBuild.versionCode,
                appVersionName = appBuild.versionName,
                account = account,
            )

            if (password == null && keyEncoded == null) {
                return@withContext BackupContentCreateResult(backupContent)
            }

            val dataEncrypted = backupCipher.encrypt(
                reference = BackupContent.Reference,
                services = json.encodeToString(backupContent.services),
                password = password,
                keyEncoded = keyEncoded,
                saltEncoded = saltEncoded,
            )

            BackupContentCreateResult(
                backupContent = backupContent.copy(
                    services = emptyList(),
                    servicesEncrypted = dataEncrypted.services.value,
                    reference = dataEncrypted.reference.value,
                ),
                keyEncoded = dataEncrypted.keyEncoded,
                saltEncoded = dataEncrypted.saltEncoded,
            )
        }
    }

    override suspend fun createBackupContentSerialized(
        password: String?,
        keyEncoded: String?,
        saltEncoded: String?,
        account: String?,
    ): String {
        return serializeBackupContent(createBackupContent(password, keyEncoded, saltEncoded, account).backupContent)
    }

    override suspend fun readBackupContent(
        fileUri: Uri,
    ): BackupContent {
        return withContext(dispatchers.io) {
            val size = context.contentResolver.openAssetFileDescriptor(fileUri, "r")!!.use { it.length }

            // Ignore too big files
            if (size > 10 * 1024 * 1024) {
                throw FileTooBigException()
            }

            // Deserialize content from file
            context.contentResolver.openInputStream(fileUri)!!.use {
                val contentSerialized = it.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText)
                json.decodeFromString<BackupContent>(contentSerialized)
            }
        }
    }

    override suspend fun decryptBackupContent(
        backupContent: BackupContent,
        password: String?,
        keyEncoded: String?,
    ): BackupContent {
        return withContext(dispatchers.io) {
            try {
                // Decrypt reference and services
                val decryptedContent = backupCipher.decrypt(
                    reference = DataEncrypted(backupContent.reference!!),
                    services = DataEncrypted(backupContent.servicesEncrypted!!),
                    password = password,
                    keyEncoded = keyEncoded,
                )

                // If success return backup content with replaced services to decrypted ones
                backupContent.copy(
                    services = json.decodeFromString(decryptedContent.services),
                    servicesEncrypted = null,
                    reference = null,
                )

            } catch (e: Exception) {
                if (e is AEADBadTagException) {
                    throw DecryptWrongPassword()
                } else {
                    throw e
                }
            }
        }
    }

    override suspend fun import(backupContent: BackupContent) {
        withContext(dispatchers.io) {

            // Import groups
            backupContent.groups.forEach { group ->
                groupsRepository.addGroup(group.asDomain())
            }

            // Import services
            val localSecrets = servicesRepository.getServices().map { it.secret.lowercase() }

            val servicesToImport = backupContent.services
                .filter { localSecrets.contains(it.secret.lowercase()).not() }
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

                    it.asDomain(
                        serviceTypeIdFromLegacy = serviceTypeIdFromLegacy,
                        iconCollectionIdFromLegacy = iconCollectionIdFromLegacy
                    )
                }

            servicesToImport.forEach { service ->
                // Check if it's valid in order not to break the local db
                // Throw if one of the services is invalid
                servicesRepository.checkServiceValid(service)
            }

            servicesRepository.addServices(servicesToImport)
        }
    }

    override suspend fun setCloudSyncActive(email: String) {
        remoteBackupStatusPreference.put(
            RemoteBackupStatusEntity(
                syncProvider = RemoteBackupStatusEntity.SyncProvider.GOOGLE_DRIVE,
                state = RemoteBackupStatusEntity.State.ACTIVE,
                account = email,
                schemaVersion = BackupContent.CurrentSchema,
            )
        )
    }

    override suspend fun setCloudSyncNotConfigured() {
        remoteBackupStatusPreference.put {
            it.copy(state = RemoteBackupStatusEntity.State.NOT_CONFIGURED, reference = null)
        }
        remoteBackupKeyPreference.delete()
    }

    override suspend fun getCloudBackup(password: String?): CloudBackupGetResult {
        return when (val result = googleDrive.getBackupFile()) {
            is GoogleDriveFileResult.Success -> {
                // File does not exists - create a new one
                if (result.fileContent.isBlank()) {
                    return CloudBackupGetResult.Success(BackupContent.Empty)
                }

                try {
                    val backupContent = json.decodeFromString<BackupContent>(result.fileContent)
                    remoteBackupStatusPreference.put {
                        it.copy(reference = backupContent.reference)
                    }

                    // Decrypt backup
                    if (backupContent.isEncrypted) {
                        if (password.isNullOrEmpty() && remoteBackupKeyPreference.get().isSet().not()) {
                            // No password provided
                            CloudBackupGetResult.Failure(CloudSyncError.DecryptNoPassword)
                        } else {

                            try {
                                CloudBackupGetResult.Success(
                                    decryptBackupContent(
                                        backupContent = backupContent,
                                        password = password,
                                        keyEncoded = remoteBackupKeyPreference.get().keyEncoded,
                                    )
                                )
                            } catch (e: Exception) {
                                // Handle decrypt error
                                when (e) {
                                    is DecryptWrongPassword -> CloudBackupGetResult.Failure(CloudSyncError.DecryptWrongPassword)
                                    else -> CloudBackupGetResult.Failure(CloudSyncError.DecryptUnknownFailure)
                                }
                            }
                        }
                    } else {
                        remoteBackupKeyPreference.delete()
                        CloudBackupGetResult.Success(backupContent)
                    }
                } catch (e: Exception) {
                    // Handle other errors (most likely serialization ones)
                    CloudBackupGetResult.Failure(CloudSyncError.JsonParsingFailure)
                }
            }

            is GoogleDriveFileResult.Failure -> {
                CloudBackupGetResult.Failure(
                    result.error.asDomain(),
                )
            }
        }
    }

    override suspend fun updateCloudBackup(
        password: String?,
        keyEncoded: String?,
        saltEncoded: String?,
        firstConnect: Boolean,
        updatedAt: Long
    ): CloudBackupUpdateResult {
        return withContext(dispatchers.io) {
            try {
                val backupContentCreateResult = createBackupContent(
                    password = password,
                    keyEncoded = keyEncoded,
                    saltEncoded = saltEncoded,
                    account = remoteBackupStatusPreference.get().account,
                )

                val backupContent = if (firstConnect) {
                    backupContentCreateResult.backupContent.copy(
                        services = backupContentCreateResult.backupContent.services.map { service -> service.copy(updatedAt = updatedAt) }
                    )
                } else {
                    backupContentCreateResult.backupContent
                }

                if (backupContent.isEncrypted) {
                    remoteBackupKeyPreference.put {
                        it.copy(
                            saltEncoded = backupContentCreateResult.saltEncoded.orEmpty(),
                            keyEncoded = backupContentCreateResult.keyEncoded.orEmpty(),
                        )
                    }
                    remoteBackupStatusPreference.put {
                        it.copy(
                            reference = backupContentCreateResult.backupContent.reference,
                            lastSyncMillis = backupContent.updatedAt
                        )
                    }
                } else {
                    remoteBackupStatusPreference.put { it.copy(reference = null, lastSyncMillis = backupContent.updatedAt) }
                    remoteBackupKeyPreference.delete()
                }

                when (val updateResult = googleDrive.updateBackupFile(
                    serializeBackupContent(
                        backupContent.copy(
                            updatedAt = updatedAt
                        )
                    )
                )) {
                    is GoogleDriveResult.Success -> {
                        CloudBackupUpdateResult.Success
                    }

                    is GoogleDriveResult.Failure -> {
                        CloudBackupUpdateResult.Failure(error = updateResult.error.asDomain())
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                CloudBackupUpdateResult.Failure(error = CloudSyncError.EncryptUnknownFailure)
            }
        }
    }

    override suspend fun checkCloudBackupPassword(password: String?): Boolean {
        return try {
            val referenceEncrypted = remoteBackupStatusPreference.get().reference!!
            val result = backupCipher.decrypt(
                dataEncrypted = DataEncrypted(referenceEncrypted),
                password = password,
                keyEncoded = null
            )
            val isCorrect = result.data == BackupContent.Reference

            if (isCorrect) {
                remoteBackupKeyPreference.put {
                    RemoteBackupKey(
                        saltEncoded = result.saltEncoded,
                        keyEncoded = result.keyEncoded,
                    )
                }
            } else {
                remoteBackupKeyPreference.delete()
            }

            return isCorrect

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun observeCloudBackupStatus(): Flow<CloudBackupStatus> {
        return remoteBackupStatusPreference.flow(emitOnSubscribe = true).map {
            CloudBackupStatus(
                active = it.state == RemoteBackupStatusEntity.State.ACTIVE,
                account = it.account,
                lastSyncMillis = it.lastSyncMillis,
                reference = it.reference,
            )
        }
    }

    override fun observeCloudSyncStatus(): Flow<CloudSyncStatus> {
        return cloudSyncStatusFlow
    }

    override fun publishCloudSyncStatus(status: CloudSyncStatus) {
        cloudSyncStatusFlow.tryEmit(status)
    }

    override fun setPasswordForCloudSync(password: String?) {
        passwordForCloudSync.tryEmit(password)
    }

    override fun observePasswordForCloudSync(): Flow<String?> {
        return passwordForCloudSync
    }

    private fun serializeBackupContent(backupContent: BackupContent): String {
        return json.encodeToString(backupContent)
    }
}