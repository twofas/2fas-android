package com.twofasapp

import com.twofasapp.backup.domain.ExportBackupToDisk
import com.twofasapp.backup.ui.export.ExportBackup
import com.twofasapp.base.AuthTracker
import com.twofasapp.features.backup.import.ImportBackup
import com.twofasapp.features.backup.import.ImportBackupFromDisk
import com.twofasapp.features.services.ServicesProxy
import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.services.domain.ConvertOtpLinkToService
import com.twofasapp.services.domain.GenerateTotp
import com.twofasapp.services.domain.GetServicesUseCase
import com.twofasapp.services.domain.ShowBackupNotice
import com.twofasapp.services.domain.StoreHotpServices
import com.twofasapp.services.domain.StoreServicesOrder
import com.twofasapp.services.googleauth.usecases.GetAccountCredentials
import com.twofasapp.start.domain.MigrateBoxToRoomCase
import com.twofasapp.usecases.app.CheckConnectionStatus
import com.twofasapp.usecases.app.FirstCodeAdded
import com.twofasapp.usecases.app.MigrateBoxToRoomCaseImpl
import com.twofasapp.usecases.backup.ObserveSyncStatus
import com.twofasapp.usecases.backup.SyncBackupServices
import com.twofasapp.usecases.onboard.OnboardingStatus
import com.twofasapp.usecases.rateapp.RateAppCondition
import com.twofasapp.usecases.rateapp.UpdateRateAppStatus
import com.twofasapp.usecases.services.AddService
import com.twofasapp.usecases.services.CheckServiceExists
import com.twofasapp.usecases.services.DeleteDuplicatedService
import com.twofasapp.usecases.services.EditStateObserver
import com.twofasapp.usecases.services.GetService
import com.twofasapp.usecases.services.GetServices
import com.twofasapp.usecases.services.GetServicesIncludingTrashed
import com.twofasapp.usecases.services.GetTrashedServices
import com.twofasapp.usecases.services.PinOptionsUseCase
import com.twofasapp.usecases.services.RestoreService
import com.twofasapp.usecases.services.SearchStateObserver
import com.twofasapp.usecases.services.ServicesModelMapper
import com.twofasapp.usecases.services.ServicesObserver
import com.twofasapp.usecases.services.ServicesRefreshTrigger
import com.twofasapp.usecases.services.StoreRecentlyDeleted
import com.twofasapp.usecases.services.TrashService
import com.twofasapp.usecases.services.UpdateServiceGroup
import com.twofasapp.usecases.settings.CheckBiometricAvailability
import com.twofasapp.usecases.settings.GetPinCode
import com.twofasapp.usecases.settings.MigratePinToKeystore
import com.twofasapp.usecases.settings.SaveLockStatus
import com.twofasapp.usecases.settings.SavePinCode
import com.twofasapp.usecases.totp.ParseOtpAuthLink
import com.twofasapp.usecases.widgets.DeactivateAllWidgets
import com.twofasapp.usecases.widgets.DeleteServiceFromWidget
import com.twofasapp.usecases.widgets.DeleteWidget
import com.twofasapp.usecases.widgets.GetWidgetSettings
import com.twofasapp.usecases.widgets.UpdateWidget
import com.twofasapp.widgets.adapter.WidgetViewsData
import com.twofasapp.widgets.adapter.WidgetViewsDataCached
import com.twofasapp.widgets.broadcast.WidgetBroadcaster
import com.twofasapp.widgets.broadcast.WidgetBroadcasterImpl
import com.twofasapp.widgets.presenter.WidgetItemPresenterDelegate
import com.twofasapp.widgets.presenter.WidgetItemPresenterDelegateImpl
import com.twofasapp.widgets.presenter.WidgetPresenter
import com.twofasapp.widgets.presenter.WidgetPresenterDelegate
import com.twofasapp.widgets.presenter.WidgetPresenterDelegateImpl
import com.twofasapp.widgets.presenter.WidgetReceiverPresenterDelegate
import com.twofasapp.widgets.presenter.WidgetReceiverPresenterDelegateImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import javax.inject.Provider

val useCaseModule = module {
    single { ParseOtpAuthLink() }
    single { AddService(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<GetServicesUseCase> { GetServices(get()) }
    single { GetServices(get()) }
    single { GetServicesIncludingTrashed(get()) }
    single { GetTrashedServices(get()) }
    single { ServicesObserver(get()) }
    single { GetService(get()) }
    single { TrashService(get(), get(), get(), get(), get(), get(), get(), get()) }
    single { RestoreService(get(), get(), get(), get(), get(), get(), get()) }
    single { UpdateServiceGroup(get()) }
    single { DeleteDuplicatedService(get(), get()) }
    single { ConvertOtpLinkToService() }
    single { CheckServiceExists(get()) }
    single { GenerateTotp(get()) }
    single { ServicesModelMapper(androidContext(), get(), get(), get(), get(), get(), get(), get()) }
    single { CheckLockStatus(get()) }
    single { SaveLockStatus(get(), get()) }
    single { GetPinCode(get(), get(), get()) }
    single { SavePinCode(get()) }
    single { CheckBiometricAvailability(androidContext()) }
    single { RateAppCondition(get()) }
    single { UpdateRateAppStatus(get(), get()) }
    single { OnboardingStatus(get()) }
    single { StoreServicesOrder(get()) }
    single { AuthTracker(Provider { get() }) }
    single { MigratePinToKeystore(get(), get(), get(), get(), get()) }
    single { GetAccountCredentials(androidContext()) }
    single { CheckConnectionStatus(androidContext()) }
    single<MigrateBoxToRoomCase> { MigrateBoxToRoomCaseImpl(androidContext(), get(), get(), get(), get(), get()) }
    single { ShowBackupNotice(get()) }
    single { FirstCodeAdded(get()) }
    single { StoreGroups(get(), get()) }
    single { SyncBackupServices(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<ExportBackup> { ExportBackupToDisk(androidContext(), get(), get(), get(), get(), get(), get(), get()) }
    single<ImportBackup> { ImportBackupFromDisk(androidContext(), get(), get(), get(), get(), get(), get()) }
    single { EditStateObserver() }
    single { SearchStateObserver() }
    single { ServicesProxy() }
    single { StoreHotpServices(get()) }
    single { ServicesRefreshTrigger() }
    single { PinOptionsUseCase(get()) }
    single { UpdateWidget(get()) }
    single { DeleteWidget(get()) }
    single { DeleteServiceFromWidget(get()) }
    single { GetWidgetSettings(get()) }
    single { DeactivateAllWidgets(get()) }
    factory<WidgetViewsData> { WidgetViewsDataCached(get(), get(), get()) }
    single<WidgetBroadcaster> { WidgetBroadcasterImpl(androidContext()) }
    single { WidgetPresenter(get(), get(), get()) }
    single<WidgetPresenterDelegate> { WidgetPresenterDelegateImpl(androidContext(), get()) }
    single<WidgetItemPresenterDelegate> { WidgetItemPresenterDelegateImpl(androidContext(), get()) }
    single<WidgetReceiverPresenterDelegate> { WidgetReceiverPresenterDelegateImpl(androidContext(), get(), get(), get(), get(), get()) }
    single { StoreRecentlyDeleted(get()) }
    single { ObserveSyncStatus() }
}