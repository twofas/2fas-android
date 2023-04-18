package com.twofasapp.backup.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.backup.EncryptBackup
import com.twofasapp.backup.domain.converter.toRemoteService
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.prefs.model.RemoteBackup
import com.twofasapp.prefs.model.RemoteGroup
import com.twofasapp.prefs.model.RemoteService
import com.twofasapp.serialization.JsonSerializer
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.GetServicesCase
import com.twofasapp.time.domain.TimeProvider
import kotlinx.coroutines.flow.firstOrNull

class ExportBackupSuspended(
    private val context: Context,
    private val timeProvider: TimeProvider,
    private val appBuild: AppBuild,
    private val servicesRepository: ServicesRepository,
    private val getServicesCase: GetServicesCase,
    private val groupsRepository: GroupsRepository,
    private val encryptBackup: EncryptBackup,
    private val jsonSerializer: JsonSerializer,
) {

    data class Params(
        val fileUri: Uri?,
        val password: String?,
    )

    sealed interface Result {
        data class Success(val backupContent: String) : Result
        data class Error(val throwable: Throwable) : Result
    }

    suspend operator fun invoke(fileUri: Uri?, password: String?): Result {
        val services = getServicesCase()
        val servicesOrder = servicesRepository.getServicesOrder()
        val groups = groupsRepository.observeGroups().firstOrNull().orEmpty()

        val remoteBackup = RemoteBackup(
            services = services.map {
                it.toRemoteService()
                    .copy(order = RemoteService.Order(position = servicesOrder.ids.indexOf(it.id)))
            },
            updatedAt = timeProvider.systemCurrentTime(),
            appVersionCode = appBuild.versionCode,
            appVersionName = appBuild.versionName,
            groups = groups.filter { group -> group.id != null }.map { group ->
                RemoteGroup(
                    id = group.id.orEmpty(),
                    name = group.name.orEmpty(),
                    isExpanded = group.isExpanded,
                    updatedAt = group.updatedAt,

                    )
            },
            account = null,
        )

        val result = encryptBackup.execute(
            EncryptBackup.Params(
                backup = remoteBackup,
                password = password,
                saltEncoded = null,
                keyEncoded = null,
            )
        ).blockingGet()

        return when (result) {
            is EncryptBackup.Result.Success -> {
                val json = jsonSerializer.serializePretty(result.encryptedRemoteBackup)

                if (fileUri != null) {
                    val outputStream = context.contentResolver.openOutputStream(fileUri)
                    outputStream?.use { it.write(json.toByteArray(Charsets.UTF_8)) }
                }

                Result.Success(json)
            }

            is EncryptBackup.Result.Error -> Result.Error(result.throwable)
        }
    }
}
