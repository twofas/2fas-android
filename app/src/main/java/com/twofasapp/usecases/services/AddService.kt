package com.twofasapp.usecases.services

import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.core.analytics.AnalyticsEvent
import com.twofasapp.core.analytics.AnalyticsParam
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.GenerateTotp
import com.twofasapp.services.domain.ShowBackupNotice
import com.twofasapp.services.domain.StoreHotpServices
import com.twofasapp.services.domain.StoreServicesOrder
import com.twofasapp.services.domain.model.Service
import com.twofasapp.time.domain.TimeProvider
import com.twofasapp.usecases.app.FirstCodeAdded
import io.reactivex.Completable
import io.reactivex.Scheduler

class AddService(
    private val servicesRepository: ServicesRepository,
    private val deleteDuplicatedService: DeleteDuplicatedService,
    private val generateTotp: GenerateTotp,
    private val analyticsService: AnalyticsService,
    private val storeServicesOrder: StoreServicesOrder,
    private val timeProvider: TimeProvider,
    private val firstCodeAdded: FirstCodeAdded,
    private val showBackupNotice: ShowBackupNotice,
    private val storeHotpServices: StoreHotpServices,
) : UseCaseParameterized<AddService.Params, Completable> {

    enum class Trigger { DEFAULT, ADD_FROM_BACKUP }

    data class Params(
        val service: ServiceDto,
        val trigger: Trigger = Trigger.DEFAULT,
    )

    override fun execute(params: Params, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Completable {
        return Completable.fromCallable {
            generateTotp.calculateCode(
                secret = params.service.secret,
                otpDigits = params.service.getDigits(),
                otpPeriod = params.service.getPeriod(),
                otpAlgorithm = params.service.getAlgorithm(),
                counter = when (params.service.authType) {
                    ServiceDto.AuthType.TOTP -> null
                    ServiceDto.AuthType.HOTP -> params.service.hotpCounter?.toLong() ?: Service.DefaultHotpCounter.toLong()
                }
            )
        }
            .andThen(deleteDuplicatedService.execute(params.service.secret, subscribeScheduler, subscribeScheduler))
            .andThen(
                servicesRepository.insertService(
                    params.service.copy(
                        id = 0,
                        backupSyncStatus = com.twofasapp.prefs.model.BackupSyncStatus.NOT_SYNCED,
                        updatedAt = timeProvider.systemCurrentTime(),
                    )
                )
            )
            .doOnSuccess { id ->
                storeServicesOrder.addToOrder(id)

                if (params.service.authType == ServiceDto.AuthType.HOTP && params.trigger != Trigger.ADD_FROM_BACKUP) {
                    storeHotpServices.onServiceAdded(params.service)
                }

                if (params.trigger != Trigger.ADD_FROM_BACKUP) {
                    analyticsService.captureEvent(
                        AnalyticsEvent.CODE_TYPE_ADDED,
                        AnalyticsParam.TYPE to params.service.authType.name
                    )
                }

                if (params.service.serviceTypeId.isNullOrBlank() && params.service.otpIssuer.isNullOrBlank()
                        .not() && params.service.source == ServiceDto.Source.Link
                ) {
                    analyticsService.captureEvent(
                        AnalyticsEvent.MISSING_ICON, AnalyticsParam.TYPE to params.service.otpIssuer
                    )
                }

                if (params.service.serviceTypeId.isNullOrBlank().not() && params.service.source == ServiceDto.Source.Link) {
                    analyticsService.captureEvent(
                        AnalyticsEvent.SUPPORTED_ICON, AnalyticsParam.TYPE to (params.service.otpIssuer ?: params.service.name)
                    )
                }
            }
            .flatMapCompletable {
                Completable.fromCallable {
                    if (firstCodeAdded.isAdded().not()) {
                        analyticsService.captureEvent(AnalyticsEvent.FIRST_CODE)
                        showBackupNotice.save(true)
                        firstCodeAdded.save(true)
                    }
                }
            }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}