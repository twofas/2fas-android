package com.twofasapp

import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.base.ActivityProvider
import com.twofasapp.base.ActivityProvider.Companion.activityContext
import com.twofasapp.base.dispatcher.AppDispatchers
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.core.cipher.CipherService
import com.twofasapp.core.cipher.CipherServiceImpl
import com.twofasapp.permissions.CameraPermissionRequest
import com.twofasapp.permissions.CameraPermissionRequestFlow
import com.twofasapp.services.analytics.AnalyticsServiceFirebase
import com.twofasapp.services.googledrive.GoogleDriveService
import com.twofasapp.services.googledrive.GoogleDriveServiceImpl
import com.twofasapp.services.workmanager.SyncBackupWorkDispatcherImpl
import com.twofasapp.services.workmanager.WipeGoogleDriveWorkDispatcher
import com.twofasapp.widgets.WidgetActionsImpl
import com.twofasapp.widgets.domain.WidgetActions
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val applicationModule = module {
    single<GoogleDriveService> { GoogleDriveServiceImpl(androidContext(), get()) }

    single<SyncBackupWorkDispatcher> { SyncBackupWorkDispatcherImpl(androidContext(), get()) }
    single { WipeGoogleDriveWorkDispatcher(androidContext()) }

    single<com.twofasapp.core.analytics.AnalyticsService> { AnalyticsServiceFirebase().apply { init(androidContext()) } }

    single<CipherService> { CipherServiceImpl() }

    single<com.twofasapp.backup.data.FilesProvider> { com.twofasapp.backup.data.FilesProviderImpl(androidContext()) }

    single { ActivityProvider() }

    singleOf(::WidgetActionsImpl) { bind<WidgetActions>() }

    factory { CameraPermissionRequest(activityContext()) }
    factory { CameraPermissionRequestFlow(activityContext()) }

    single<Dispatchers> { AppDispatchers() }

}