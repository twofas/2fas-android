package com.twofasapp.usecases.services

import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.core.analytics.AnalyticsEvent
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.GenerateTotp
import com.twofasapp.services.domain.ShowBackupNotice
import com.twofasapp.services.domain.StoreHotpServices
import com.twofasapp.services.domain.StoreServicesOrder
import com.twofasapp.services.domain.model.Service
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
                        backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                        updatedAt = timeProvider.systemCurrentTime(),
                    )
                )
            )
            .doOnSuccess { id ->
                storeServicesOrder.addToOrder(id)

                if (params.service.authType == ServiceDto.AuthType.HOTP && params.trigger != Trigger.ADD_FROM_BACKUP) {
                    storeHotpServices.onServiceAdded(params.service)
                }
            }
            .flatMapCompletable {
                Completable.fromCallable {
                    if (firstCodeAdded.isAdded().not()) {
                        showBackupNotice.save(true)
                    }
                }
            }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}