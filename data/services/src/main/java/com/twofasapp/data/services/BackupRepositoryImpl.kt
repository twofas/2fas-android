package com.twofasapp.data.services

import android.app.Application
import android.net.Uri
import com.twofasapp.cipher.backup.BackupCipher
import com.twofasapp.cipher.backup.DataEncrypted
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.BackupContent
import com.twofasapp.data.services.exceptions.FileTooBigException
import com.twofasapp.data.services.exceptions.ImportNoPassword
import com.twofasapp.data.services.exceptions.ImportWrongPassword
import com.twofasapp.data.services.mapper.asBackup
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.parsers.LegacyTypeToId
import com.twofasapp.parsers.ServiceIcons
import kotlinx.coroutines.flow.first
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
) : BackupRepository {

    override suspend fun createBackupContent(
        password: String?,
        keyEncoded: String?,
        saltEncoded: String?
    ): BackupContent {
        return withContext(dispatchers.io) {
            val groups = groupsRepository.observeGroups().first().asBackup()
            val services = servicesRepository.getServices().asBackup()

            val backupContent = BackupContent(
                services = services,
                groups = groups,
                updatedAt = timeProvider.systemCurrentTime(),
                appVersionCode = appBuild.versionCode,
                appVersionName = appBuild.versionName,
                account = null,
            )

            if (password == null && keyEncoded == null) {
                return@withContext backupContent
            }

            val dataEncrypted = backupCipher.encrypt(
                reference = BackupContent.Reference,
                services = json.encodeToString(backupContent.services),
                password = password,
                keyEncoded = keyEncoded,
                saltEncoded = saltEncoded,
            )

            backupContent.copy(
                services = emptyList(),
                servicesEncrypted = dataEncrypted.services.value,
                reference = dataEncrypted.reference.value,
            )
        }
    }

    override suspend fun createBackupContentSerialized(
        password: String?,
        keyEncoded: String?,
        saltEncoded: String?
    ): String {
        return json.encodeToString(createBackupContent(password, keyEncoded, saltEncoded))
    }

    override suspend fun readBackupContent(
        fileUri: Uri,
        password: String?,
    ): BackupContent {
        return withContext(dispatchers.io) {
            val size = context.contentResolver.openAssetFileDescriptor(fileUri, "r")!!.use { it.length }

            // Ignore too big files
            if (size > 10 * 1024 * 1024) {
                throw FileTooBigException()
            }

            // Deserialize content from file
            val backupContent = context.contentResolver.openInputStream(fileUri)!!.use {
                val contentSerialized = it.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText)
                json.decodeFromString<BackupContent>(contentSerialized)
            }

            // If reference is null it means that backup is not encrypted, just return it
            if (backupContent.isEncrypted.not()) {
                return@withContext backupContent
            }

            // Backup is encrypted but there is no password provided
            if (password.isNullOrBlank()) {
                throw ImportNoPassword(backupContent)
            }

            decryptBackupContent(
                backupContent = backupContent,
                password = password,
            )
        }
    }

    override suspend fun decryptBackupContent(
        backupContent: BackupContent,
        password: String,
    ): BackupContent {
        return withContext(dispatchers.io) {

            // Decrypt reference and services
            val decryptedContent = try {
                backupCipher.decrypt(
                    reference = DataEncrypted(backupContent.reference!!),
                    services = DataEncrypted(backupContent.servicesEncrypted!!),
                    password = password,
                )
            } catch (e: Exception) {
                if (e is AEADBadTagException) {
                    throw ImportWrongPassword()
                } else {
                    throw e
                }
            }

            // If success return backup content with replaced services to decrypted ones
            backupContent.copy(
                services = json.decodeFromString(decryptedContent.services),
                servicesEncrypted = null,
                reference = null,
            )
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

            servicesRepository.addServices(servicesToImport)
        }
    }
}