package com.twofasapp.usecases.app

import android.content.Context
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.entity.MyObjectBox
import com.twofasapp.entity.Service
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.usecase.MigratedToRoomPreference
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.StoreServicesOrder
import com.twofasapp.usecases.MigrateBoxToRoomCase

class MigrateBoxToRoomCaseImpl(
    private val context: Context,
    private val servicesRepository: ServicesRepository,
    private val migratedToRoomPreference: MigratedToRoomPreference,
    private val storeServicesOrder: StoreServicesOrder,
    private val timeProvider: TimeProvider,
) : MigrateBoxToRoomCase {

    private val boxService by lazy {
        MyObjectBox.builder().androidContext(context)
            .build()
            .boxFor(Service::class.java)
    }

    override suspend fun invoke() {
        try {
            if (migratedToRoomPreference.get()) {
                return
            }

            if (context.filesDir?.list()?.contains("objectbox") == false) {
                migratedToRoomPreference.put(true)
                return
            }

            val boxServices = boxService.all.sortedBy { model -> storeServicesOrder.getOrder().ids.indexOf(model.id) }

            if (boxServices.isEmpty()) {
                migratedToRoomPreference.put(true)
                return
            }

            val ids = mutableListOf<Long>()
            val now = timeProvider.systemCurrentTime()

            boxServices.forEach {
                val id = servicesRepository.insertService(
                    com.twofasapp.services.domain.model.Service(
                        id = 0,
                        name = it.name,
                        secret = it.secret,
                        authType = com.twofasapp.services.domain.model.Service.AuthType.TOTP,
                        otp = com.twofasapp.services.domain.model.Service.Otp(
                            label = it.label.orEmpty(),
                            account = it.account.orEmpty(),
                            issuer = it.issuer,
                        ),
                        backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                        updatedAt = now,
                        assignedDomains = emptyList(),
                        serviceTypeId = null,
                        iconCollectionId = ServiceIcons.defaultCollectionId,
                        source = com.twofasapp.services.domain.model.Service.Source.Manual,
                    )
                )

                ids.add(id)
            }

            val roomServices = servicesRepository.select().blockingGet()
            val isSuccess = roomServices.map { it.secret.lowercase().trim() }.distinct()
                .containsAll(boxServices.map { it.secret.lowercase().trim() }.distinct())
            storeServicesOrder.saveOrder(storeServicesOrder.getOrder().copy(ids = ids))
            migratedToRoomPreference.put(isSuccess)
        } catch (e: Exception) {

        }
    }
}