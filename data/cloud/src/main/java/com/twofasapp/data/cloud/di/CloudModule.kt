package com.twofasapp.data.cloud.di

import com.twofasapp.data.cloud.googleauth.GoogleAuth
import com.twofasapp.data.cloud.googleauth.GoogleAuthImpl
import com.twofasapp.data.cloud.googledrive.GoogleDrive
import com.twofasapp.data.cloud.googledrive.GoogleDriveImpl
import com.twofasapp.di.KoinModule
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class CloudModule : KoinModule {
    override fun provide() = module {
        singleOf(::GoogleAuthImpl) { bind<GoogleAuth>() }
        singleOf(::GoogleDriveImpl) { bind<GoogleDrive>() }
    }
}