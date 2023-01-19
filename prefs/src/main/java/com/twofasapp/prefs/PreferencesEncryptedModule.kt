package com.twofasapp.prefs

import com.twofasapp.di.KoinModule
import com.twofasapp.prefs.usecase.DatabaseMasterKeyPreference
import com.twofasapp.prefs.usecase.InvalidPinStatusPreference
import com.twofasapp.prefs.usecase.PinOptionsPreference
import com.twofasapp.prefs.usecase.PinSecuredPreference
import com.twofasapp.prefs.usecase.RecentlyDeletedPreference
import com.twofasapp.prefs.usecase.RemoteBackupKeyPreference
import com.twofasapp.storage.EncryptedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class PreferencesEncryptedModule : KoinModule {

    override fun provide() = module {
        single { PinSecuredPreference(androidContext(), get<EncryptedPreferences>()) }
        single { RecentlyDeletedPreference(get<EncryptedPreferences>()) }
        single { PinOptionsPreference(get<EncryptedPreferences>()) }
        single { InvalidPinStatusPreference(get<EncryptedPreferences>()) }
        single { DatabaseMasterKeyPreference(get<EncryptedPreferences>()) }
        single { RemoteBackupKeyPreference(get<EncryptedPreferences>()) }
    }
}