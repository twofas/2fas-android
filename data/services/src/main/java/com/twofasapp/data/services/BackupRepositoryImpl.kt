package com.twofasapp.data.services

import com.twofasapp.cipher.backup.BackupCipher
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.BackupContent
import com.twofasapp.data.services.mapper.asBackup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackupRepositoryImpl(
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
}