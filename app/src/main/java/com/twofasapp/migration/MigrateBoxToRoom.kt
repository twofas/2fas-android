package com.twofasapp.migration

import android.content.Context
import com.twofasapp.common.domain.BackupSyncStatus
import com.twofasapp.common.ktx.enumValueOrNull
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.legacy.objectbox.MyObjectBox
import com.twofasapp.legacy.objectbox.Service
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.usecase.MigratedToRoomPreference

class MigrateBoxToRoom(
    private val context: Context,
    private val servicesRepository: ServicesRepository,
    private val migratedToRoomPreference: MigratedToRoomPreference,
    private val timeProvider: TimeProvider,
) {

    private val boxService by lazy {
        MyObjectBox.builder().androidContext(context)
            .build()
            .boxFor(Service::class.java)
    }

    suspend fun invoke() {
        try {
            if (migratedToRoomPreference.get()) {
                return
            }

            if (context.filesDir?.list()?.contains("objectbox") == false) {
                migratedToRoomPreference.put(true)
                return
            }

            val boxServices = boxService.all

            if (boxServices.isEmpty()) {
                migratedToRoomPreference.put(true)
                return
            }

            val ids = mutableListOf<Long>()
            val now = timeProvider.systemCurrentTime()

            servicesRepository.addServices(
                boxServices.map {
                    com.twofasapp.common.domain.Service(
                        id = 0,
                        name = it.name,
                        secret = it.secret,
                        authType = com.twofasapp.common.domain.Service.AuthType.TOTP,
                        backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                        updatedAt = now,
                        assignedDomains = emptyList(),
                        serviceTypeId = null,
                        iconCollectionId = ServiceIcons.defaultCollectionId,
                        source = com.twofasapp.common.domain.Service.Source.Manual,
                        info = it.label,
                        link = null,
                        issuer = it.issuer,
                        period = it.period,
                        digits = it.digits,
                        algorithm = enumValueOrNull(it.algorithm),
                        iconLight = "",
                        iconDark = "",
                        badgeColor = null,
                        tags = listOf(),
                        isDeleted = false,
                    )
                }
            )

            val roomServices = servicesRepository.getServices()
            val isSuccess = roomServices.map { it.secret.lowercase().trim() }.distinct()
                .containsAll(boxServices.map { it.secret.lowercase().trim() }.distinct())
            migratedToRoomPreference.put(isSuccess)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}