package com.twofasapp

import com.twofasapp.base.ActivityProvider
import com.twofasapp.base.AuthTracker
import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.services.domain.ConvertOtpLinkToService
import com.twofasapp.services.domain.GenerateTotp
import com.twofasapp.services.domain.ShowBackupNotice
import com.twofasapp.services.domain.StoreHotpServices
import com.twofasapp.services.domain.StoreServicesOrder
import com.twofasapp.usecases.MigrateBoxToRoomCase
import com.twofasapp.usecases.app.CheckConnectionStatus
import com.twofasapp.usecases.app.FirstCodeAdded
import com.twofasapp.usecases.app.MigrateBoxToRoomCaseImpl
import com.twofasapp.usecases.rateapp.RateAppCondition
import com.twofasapp.usecases.rateapp.UpdateRateAppStatus
import com.twofasapp.usecases.services.PinOptionsUseCase
import com.twofasapp.usecases.totp.ParseOtpAuthLink
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import javax.inject.Provider

val useCaseModule = module {
    single { ActivityProvider() }
    single { ParseOtpAuthLink() }
    single { ConvertOtpLinkToService() }
    single { GenerateTotp(get()) }
    single { CheckLockStatus(get()) }
    single { RateAppCondition(get()) }
    single { UpdateRateAppStatus(get(), get()) }
    single { StoreServicesOrder(get()) }
    single { AuthTracker(Provider { get() }) }
    single { CheckConnectionStatus(androidContext()) }
    single<MigrateBoxToRoomCase> { MigrateBoxToRoomCaseImpl(androidContext(), get(), get(), get(), get()) }
    single { ShowBackupNotice(get()) }
    single { FirstCodeAdded(get()) }
    single { StoreGroups(get(), get()) }
    single { StoreHotpServices(get()) }
    single { PinOptionsUseCase(get()) }
}