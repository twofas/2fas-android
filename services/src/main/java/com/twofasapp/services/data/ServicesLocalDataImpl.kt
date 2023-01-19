package com.twofasapp.services.data

import com.twofasapp.extensions.removeWhiteCharacters
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.persistence.dao.ServiceDao
import com.twofasapp.persistence.model.ServiceEntity
import com.twofasapp.prefs.model.BackupSyncStatus
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.model.Tint
import com.twofasapp.services.data.converter.toEntity
import com.twofasapp.services.data.converter.toService
import com.twofasapp.services.domain.model.Service
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

internal class ServicesLocalDataImpl(
    private val dao: ServiceDao
) : ServicesLocalData {

    override fun select(): Single<List<ServiceDto>> {
        return dao.select()
            .map { list ->
                list.map { local ->
                    ServiceDto(
                        id = local.id,
                        name = local.name,
                        secret = local.secret,
                        authType = local.authType?.let { ServiceDto.AuthType.valueOf(it) } ?: ServiceDto.AuthType.TOTP,
                        otpLabel = local.otpLabel,
                        otpAccount = local.otpAccount,
                        otpIssuer = local.otpIssuer,
                        otpDigits = local.otpDigits,
                        otpPeriod = local.otpPeriod,
                        otpAlgorithm = local.otpAlgorithm,
                        hotpCounter = local.hotpCounter,
                        backupSyncStatus = BackupSyncStatus.valueOf(local.backupSyncStatus),
                        updatedAt = local.updatedAt,
                        badge = local.badgeColor?.let { ServiceDto.Badge(Tint.valueOf(it)) },
                        selectedImageType = local.selectedImageType?.let {
                            when (it) {
                                "Brand" -> ServiceDto.ImageType.IconCollection
                                "Label" -> ServiceDto.ImageType.Label
                                else -> ServiceDto.ImageType.IconCollection
                            }
                        } ?: ServiceDto.ImageType.IconCollection,
                        labelText = local.labelText,
                        labelBackgroundColor = local.labelBackgroundColor?.let { color -> Tint.valueOf(color) },
                        iconCollectionId = local.iconCollectionId ?: ServiceIcons.defaultCollectionId,
                        groupId = local.groupId,
                        isDeleted = local.isDeleted,
                        assignedDomains = local.assignedDomains.orEmpty(),
                        serviceTypeId = local.serviceTypeId,
                        source = local.source?.let { ServiceDto.Source.valueOf(it) } ?: ServiceDto.Source.Manual
                    )
                }
            }
    }

    override suspend fun selectAll(): List<Service> {
        return dao.selectAll().map { it.toService() }
    }

    override fun selectFlow(): Flow<List<Service>> {
        return dao.selectFlow().map { list ->
            list.map { it.toService() }
        }
    }

    override fun observe(serviceId: Long): Flow<Service> {
        return dao.observe(serviceId).map { it.toService() }
    }

    override fun observe(): Flowable<List<ServiceDto>> {
        return dao.observe()
            .map { list ->
                list.map { local ->
                    ServiceDto(
                        id = local.id,
                        name = local.name,
                        secret = local.secret,
                        authType = local.authType?.let { ServiceDto.AuthType.valueOf(it) } ?: ServiceDto.AuthType.TOTP,
                        otpLabel = local.otpLabel,
                        otpAccount = local.otpAccount,
                        otpIssuer = local.otpIssuer,
                        otpDigits = local.otpDigits,
                        otpPeriod = local.otpPeriod,
                        otpAlgorithm = local.otpAlgorithm,
                        hotpCounter = local.hotpCounter,
                        backupSyncStatus = BackupSyncStatus.valueOf(local.backupSyncStatus),
                        updatedAt = local.updatedAt,
                        badge = local.badgeColor?.let { ServiceDto.Badge(Tint.valueOf(it)) },
                        selectedImageType = local.selectedImageType?.let {
                            when (it) {
                                "Brand" -> ServiceDto.ImageType.IconCollection
                                "Label" -> ServiceDto.ImageType.Label
                                else -> ServiceDto.ImageType.IconCollection
                            }
                        } ?: ServiceDto.ImageType.IconCollection,
                        labelText = local.labelText,
                        labelBackgroundColor = local.labelBackgroundColor?.let { color -> Tint.valueOf(color) },
                        iconCollectionId = local.iconCollectionId ?: ServiceIcons.defaultCollectionId,
                        groupId = local.groupId,
                        isDeleted = local.isDeleted,
                        assignedDomains = local.assignedDomains.orEmpty(),
                        serviceTypeId = local.serviceTypeId,
                        source = local.source?.let { ServiceDto.Source.valueOf(it) } ?: ServiceDto.Source.Manual
                    )
                }
            }
    }

    override fun insertService(service: ServiceDto): Single<Long> {
        Timber.d("InsertService: $service")
        return dao.insert(
            ServiceEntity(
                id = 0,
                name = service.name,
                secret = service.secret.removeWhiteCharacters(),
                serviceTypeId = service.serviceTypeId,
                iconCollectionId = service.iconCollectionId,
                source = service.source.name,
                otpLink = service.otpLink,
                otpLabel = service.otpLabel,
                otpAccount = service.otpAccount,
                otpIssuer = service.otpIssuer,
                otpDigits = service.getDigits(),
                otpPeriod = service.getPeriod(),
                otpAlgorithm = service.getAlgorithm(),
                backupSyncStatus = service.backupSyncStatus.name,
                updatedAt = service.updatedAt,
                badgeColor = service.badge?.color?.name,
                selectedImageType = service.selectedImageType.name,
                labelText = service.labelText,
                labelBackgroundColor = service.labelBackgroundColor?.name,
                groupId = service.groupId,
                isDeleted = service.isDeleted,
                authType = service.authType.name,
                hotpCounter = service.hotpCounter,
                assignedDomains = service.assignedDomains
            )
        )
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun insertService(service: Service): Long {
        Timber.d("InsertService: $service")
        return dao.insertSuspend(service.toEntity())
    }

    override fun updateService(vararg services: ServiceDto): Completable {
        Timber.d("UpdateServices: ${services.toList()}")
        return dao.update(
            *services.map {
                ServiceEntity(
                    id = it.id,
                    name = it.name,
                    secret = it.secret,
                    serviceTypeId = it.serviceTypeId,
                    iconCollectionId = it.iconCollectionId,
                    source = it.source.name,
                    otpLink = it.otpLink,
                    otpLabel = it.otpLabel,
                    otpAccount = it.otpAccount,
                    otpIssuer = it.otpIssuer,
                    otpDigits = it.getDigits(),
                    otpPeriod = it.getPeriod(),
                    otpAlgorithm = it.getAlgorithm(),
                    backupSyncStatus = it.backupSyncStatus.name,
                    updatedAt = it.updatedAt,
                    badgeColor = it.badge?.color?.name,
                    selectedImageType = it.selectedImageType.name,
                    labelText = it.labelText,
                    labelBackgroundColor = it.labelBackgroundColor?.name,
                    groupId = it.groupId,
                    isDeleted = it.isDeleted,
                    authType = it.authType.name,
                    hotpCounter = it.hotpCounter,
                    assignedDomains = it.assignedDomains,
                )
            }.toTypedArray()
        )
    }

    override suspend fun updateServiceSuspend(vararg services: Service) {
        dao.updateSuspend(*services.map { it.toEntity() }.toTypedArray())
    }

    override fun deleteService(vararg services: ServiceDto): Completable {
        Timber.d("DeleteServices: ${services.toList()}")
        return dao.deleteById(services.map { it.id })
    }

    override fun deleteService(id: Long): Completable {
        Timber.d("DeleteService: $id")
        return dao.deleteById(listOf(id))
    }
}