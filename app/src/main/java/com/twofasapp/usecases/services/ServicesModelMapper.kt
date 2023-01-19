package com.twofasapp.usecases.services

import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.entity.GroupModel
import com.twofasapp.entity.ServiceModel
import com.twofasapp.entity.Services
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.usecase.ServicesOrderPreference
import com.twofasapp.prefs.usecase.ShowNextTokenPreference
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.services.domain.GenerateTotp
import com.twofasapp.services.domain.StoreHotpServices
import com.twofasapp.services.domain.StoreServicesOrder
import com.twofasapp.services.domain.model.Service
import com.twofasapp.time.domain.TimeProvider
import io.reactivex.Scheduler
import io.reactivex.Single
import java.time.Instant

class ServicesModelMapper(
    private val generateTotp: GenerateTotp,
    private val storeServicesOrder: StoreServicesOrder,
    private val storeGroups: StoreGroups,
    private val storeHotpServices: StoreHotpServices,
    private val showNextTokenPreference: ShowNextTokenPreference,
    private val timeProvider: TimeProvider,
    private val servicesOrderPreference: ServicesOrderPreference,
) : UseCaseParameterized<List<ServiceDto>, Single<Services>> {

    override fun execute(params: List<ServiceDto>, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<Services> {
        return Single.fromCallable {

            try {
                val groups = storeGroups.all()
                val groupIds = groups.list.map { it.id }
                val services = params.sortedBy { service ->
                    when (servicesOrderPreference.get().type) {
                        com.twofasapp.prefs.model.ServicesOrder.Type.Alphabetical -> service.name.lowercase().first().code
                        com.twofasapp.prefs.model.ServicesOrder.Type.Manual -> storeServicesOrder.getOrder().ids.indexOf(service.id)
                    }
                }

                Services(
                    groups = mutableListOf<GroupModel>().apply {
                        add(createDefaultGroup(services.filter { it.groupId == null || groupIds.contains(it.groupId).not() }
                            .map { mapServiceDtoToModel(it) },
                            groups.isDefaultGroupExpanded || groups.list.isEmpty()
                        )
                        )
                        addAll(
                            groups.list.map { group ->
                                GroupModel(
                                    group = group,
                                    services = services.filter { it.groupId == group.id }.map { mapServiceDtoToModel(it) },
                                )
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Services(emptyList())
            }

        }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }

    private fun createDefaultGroup(services: List<ServiceModel>, isExpanded: Boolean): GroupModel {
        return GroupModel(
            group = com.twofasapp.prefs.model.Group(
                id = null,
                name = "My Tokens",
                isExpanded = isExpanded,
            ),
            services = services
        )
    }

    private fun mapServiceDtoToModel(serviceDto: ServiceDto): ServiceModel {
        return try {
            ServiceModel(
                service = serviceDto,
                counter = serviceDto.calculateCounter().toInt(),
                code = generateTotp.calculateCode(
                    secret = serviceDto.secret,
                    otpDigits = serviceDto.getDigits(),
                    otpPeriod = serviceDto.getPeriod(),
                    otpAlgorithm = serviceDto.getAlgorithm(),
                    counter = when (serviceDto.authType) {
                        ServiceDto.AuthType.TOTP -> null
                        ServiceDto.AuthType.HOTP -> serviceDto.hotpCounter?.toLong() ?: Service.DefaultHotpCounter.toLong()
                    }
                ),
                nextCode = generateTotp.calculateNextCode(
                    secret = serviceDto.secret,
                    otpDigits = serviceDto.getDigits(),
                    otpPeriod = serviceDto.getPeriod(),
                    otpAlgorithm = serviceDto.getAlgorithm(),
                    counter = when (serviceDto.authType) {
                        ServiceDto.AuthType.TOTP -> null
                        ServiceDto.AuthType.HOTP -> serviceDto.hotpCounter?.toLong() ?: Service.DefaultHotpCounter.toLong()
                    }
                ),
                shouldShowNextToken = showNextTokenPreference.get(),
                isHotpCodeVisible = if (serviceDto.authType == ServiceDto.AuthType.HOTP) storeHotpServices.shouldShowCode(serviceDto) else false,
                isHotpRefreshEnabled = if (serviceDto.authType == ServiceDto.AuthType.HOTP) storeHotpServices.shouldEnableRefresh(serviceDto) else true,
                tags = SupportedServices.list.firstOrNull { it.id == serviceDto.serviceTypeId }?.tags ?: emptyList()
            )
        } catch (_: Exception) {
            ServiceModel(
                service = serviceDto,
                counter = serviceDto.calculateCounter().toInt(),
                code = "000000",
                nextCode = "000000",
                shouldShowNextToken = showNextTokenPreference.get(),
                isHotpCodeVisible = if (serviceDto.authType == ServiceDto.AuthType.HOTP) storeHotpServices.shouldShowCode(serviceDto) else false,
                isHotpRefreshEnabled = if (serviceDto.authType == ServiceDto.AuthType.HOTP) storeHotpServices.shouldEnableRefresh(serviceDto) else true,
                tags = SupportedServices.list.firstOrNull { it.id == serviceDto.serviceTypeId }?.tags ?: emptyList(),
            )
        }
    }

    private fun ServiceDto.calculateCounter() =
        getPeriod() - (Instant.now().epochSecond + timeProvider.realTimeDelta() / 1000) % getPeriod()
}
