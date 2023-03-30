package com.twofasapp.data.services.local

import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.services.domain.ServicesOrder
import com.twofasapp.data.services.local.model.ServicesOrderEntity
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.data.services.mapper.asEntity
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

internal class ServicesLocalSource(
    private val json: Json,
    private val preferences: PlainPreferences,
    private val dao: ServiceDao,
) {
    companion object {
        private const val KeyOrder = "servicesOrder"
    }

    private val recentlyAddedServiceFlow: MutableSharedFlow<RecentlyAddedService> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val servicesOrderFlow: MutableStateFlow<ServicesOrderEntity> by lazy {
        MutableStateFlow(getOrder())
    }

    private fun log(msg: String) {
        Timber.tag("ServicesDao").i(msg)
    }

    fun observeServices(): Flow<List<Service>> {
        return dao.observe().map { list ->
            list.filter { it.isDeleted != true }.map { it.asDomain() }
        }
    }

    fun observeDeletedServices(): Flow<List<Service>> {
        return dao.observe().map { list ->
            list.filter { it.isDeleted == true }.map { it.asDomain() }
        }
    }

    suspend fun getServices(): List<Service> {
        return dao.select().map { it.asDomain() }
    }

    fun observeService(id: Long): Flow<Service> {
        return dao.observe(id).map { it.asDomain() }
    }

    fun observeRecentlyAddedService(): Flow<RecentlyAddedService> {
        return recentlyAddedServiceFlow.filterNotNull()
    }

    suspend fun getService(id: Long): Service {
        return dao.select(id).asDomain()
    }

    suspend fun deleteService(id: Long) {
        log("Delete service $id")
        dao.delete(id)
    }

    suspend fun updateService(service: Service) {
        dao.update(service.asEntity())
    }

    private fun getOrder(): ServicesOrderEntity {
        return preferences.getString(KeyOrder)?.let {
            json.decodeFromString(it)
        } ?: ServicesOrderEntity()
    }

    private fun saveOrder(entity: ServicesOrderEntity) {
        preferences.putString(KeyOrder, json.encodeToString(entity))
    }

    fun observeOrder(): Flow<ServicesOrder> {
//        return preferences.observe(KeyOrder, "").map { value ->
//            (value?.let {
//                try {
//                    json.decodeFromString(value)
//                } catch (e: Exception) {
//                    ServicesOrderEntity()
//                }
//            } ?: ServicesOrderEntity()).asDomain()
//        }

        return servicesOrderFlow.map { it.asDomain() }
    }

    fun deleteServiceFromOrder(id: Long) {
        val newOrder = servicesOrderFlow.value.copy(
            ids = servicesOrderFlow.value.ids.minus(id)
        )

        servicesOrderFlow.tryEmit(newOrder)
        saveOrder(newOrder)
    }

    fun addServiceToOrder(id: Long) {
        val newOrder = servicesOrderFlow.value.copy(
            ids = servicesOrderFlow.value.ids.plus(id)
        )

        servicesOrderFlow.tryEmit(newOrder)
        saveOrder(newOrder)
    }

    fun pushRecentlyAddedService(id: Long, source: RecentlyAddedService.Source) {
        GlobalScope.launch {
            recentlyAddedServiceFlow.tryEmit(
                RecentlyAddedService(
                    service = getService(id),
                    source = source,
                )
            )
        }
    }

    suspend fun incrementHotpCounter(id: Long, counter: Int, timestamp: Long) {
        dao.update(
            dao.select(id).copy(hotpCounter = counter, hotpCounterTimestamp = timestamp)
        )
    }

    suspend fun setServiceGroup(id: Long, groupId: String?) {
        dao.update(
            dao.select(id).copy(groupId = groupId)
        )
    }

    fun saveServicesOrder(ids: List<Long>) {
        val local = servicesOrderFlow.value

        local.copy(ids = ids).let {
            servicesOrderFlow.tryEmit(it)
            saveOrder(it)
        }
    }

    suspend fun cleanUpGroups(groupIds: List<String>) {
        dao.cleanUpGroups(groupIds)
    }
//
//    fun select(): Single<List<ServiceDto>> {
//        return dao.legacySelect()
//            .map { list ->
//                list.map { local ->
//                    ServiceDto(
//                        id = local.id,
//                        name = local.name,
//                        secret = local.secret,
//                        authType = local.authType?.let { ServiceDto.AuthType.valueOf(it) } ?: ServiceDto.AuthType.TOTP,
//                        otpLabel = local.otpLabel,
//                        otpAccount = local.otpAccount,
//                        otpIssuer = local.otpIssuer,
//                        otpDigits = local.otpDigits,
//                        otpPeriod = local.otpPeriod,
//                        otpAlgorithm = local.otpAlgorithm,
//                        hotpCounter = local.hotpCounter,
//                        backupSyncStatus = BackupSyncStatus.valueOf(local.backupSyncStatus),
//                        updatedAt = local.updatedAt,
//                        badge = local.badgeColor?.let { ServiceDto.Badge(Tint.valueOf(it)) },
//                        selectedImageType = local.selectedImageType?.let {
//                            when (it) {
//                                "Brand" -> ServiceDto.ImageType.IconCollection
//                                "Label" -> ServiceDto.ImageType.Label
//                                else -> ServiceDto.ImageType.IconCollection
//                            }
//                        } ?: ServiceDto.ImageType.IconCollection,
//                        labelText = local.labelText,
//                        labelBackgroundColor = local.labelBackgroundColor?.let { color -> Tint.valueOf(color) },
//                        iconCollectionId = local.iconCollectionId ?: ServiceIcons.defaultCollectionId,
//                        groupId = local.groupId,
//                        isDeleted = local.isDeleted,
//                        assignedDomains = local.assignedDomains.orEmpty(),
//                        serviceTypeId = local.serviceTypeId,
//                        source = local.source?.let { ServiceDto.Source.valueOf(it) } ?: ServiceDto.Source.Manual
//                    )
//                }
//            }
//    }
//
//    fun observe(): Flowable<List<ServiceDto>> {
//        return dao.legacyObserve()
//            .map { list ->
//                list.map { local ->
//                    ServiceDto(
//                        id = local.id,
//                        name = local.name,
//                        secret = local.secret,
//                        authType = local.authType?.let { ServiceDto.AuthType.valueOf(it) } ?: ServiceDto.AuthType.TOTP,
//                        otpLabel = local.otpLabel,
//                        otpAccount = local.otpAccount,
//                        otpIssuer = local.otpIssuer,
//                        otpDigits = local.otpDigits,
//                        otpPeriod = local.otpPeriod,
//                        otpAlgorithm = local.otpAlgorithm,
//                        hotpCounter = local.hotpCounter,
//                        backupSyncStatus = BackupSyncStatus.valueOf(local.backupSyncStatus),
//                        updatedAt = local.updatedAt,
//                        badge = local.badgeColor?.let { ServiceDto.Badge(Tint.valueOf(it)) },
//                        selectedImageType = local.selectedImageType?.let {
//                            when (it) {
//                                "Brand" -> ServiceDto.ImageType.IconCollection
//                                "Label" -> ServiceDto.ImageType.Label
//                                else -> ServiceDto.ImageType.IconCollection
//                            }
//                        } ?: ServiceDto.ImageType.IconCollection,
//                        labelText = local.labelText,
//                        labelBackgroundColor = local.labelBackgroundColor?.let { color -> Tint.valueOf(color) },
//                        iconCollectionId = local.iconCollectionId ?: ServiceIcons.defaultCollectionId,
//                        groupId = local.groupId,
//                        isDeleted = local.isDeleted,
//                        assignedDomains = local.assignedDomains.orEmpty(),
//                        serviceTypeId = local.serviceTypeId,
//                        source = local.source?.let { ServiceDto.Source.valueOf(it) } ?: ServiceDto.Source.Manual
//                    )
//                }
//            }
//    }
//
//    fun insertService(service: ServiceDto): Single<Long> {
//        Timber.d("InsertService: $service")
//        return dao.legacyInsert(
//            ServiceEntity(
//                id = 0,
//                name = service.name,
//                secret = service.secret.removeWhiteCharacters(),
//                serviceTypeId = service.serviceTypeId,
//                iconCollectionId = service.iconCollectionId,
//                source = service.source.name,
//                otpLink = service.otpLink,
//                otpLabel = service.otpLabel,
//                otpAccount = service.otpAccount,
//                otpIssuer = service.otpIssuer,
//                otpDigits = service.getDigits(),
//                otpPeriod = service.getPeriod(),
//                otpAlgorithm = service.getAlgorithm(),
//                backupSyncStatus = service.backupSyncStatus.name,
//                updatedAt = service.updatedAt,
//                badgeColor = service.badge?.color?.name,
//                selectedImageType = service.selectedImageType.name,
//                labelText = service.labelText,
//                labelBackgroundColor = service.labelBackgroundColor?.name,
//                groupId = service.groupId,
//                isDeleted = service.isDeleted,
//                authType = service.authType.name,
//                hotpCounter = service.hotpCounter,
//                assignedDomains = service.assignedDomains
//            )
//        )
//    }
//
//    fun updateService(vararg services: ServiceDto): Completable {
//        Timber.d("UpdateServices: ${services.toList()}")
//        return dao.legacyUpdate(
//            *services.map {
//                ServiceEntity(
//                    id = it.id,
//                    name = it.name,
//                    secret = it.secret,
//                    serviceTypeId = it.serviceTypeId,
//                    iconCollectionId = it.iconCollectionId,
//                    source = it.source.name,
//                    otpLink = it.otpLink,
//                    otpLabel = it.otpLabel,
//                    otpAccount = it.otpAccount,
//                    otpIssuer = it.otpIssuer,
//                    otpDigits = it.getDigits(),
//                    otpPeriod = it.getPeriod(),
//                    otpAlgorithm = it.getAlgorithm(),
//                    backupSyncStatus = it.backupSyncStatus.name,
//                    updatedAt = it.updatedAt,
//                    badgeColor = it.badge?.color?.name,
//                    selectedImageType = it.selectedImageType.name,
//                    labelText = it.labelText,
//                    labelBackgroundColor = it.labelBackgroundColor?.name,
//                    groupId = it.groupId,
//                    isDeleted = it.isDeleted,
//                    authType = it.authType.name,
//                    hotpCounter = it.hotpCounter,
//                    assignedDomains = it.assignedDomains,
//                )
//            }.toTypedArray()
//        )
//    }
}