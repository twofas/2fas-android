package com.twofasapp.storage.di

import com.twofasapp.di.KoinModule
import com.twofasapp.storage.EncryptedPreferences
import com.twofasapp.storage.EncryptedPreferencesImpl
import com.twofasapp.storage.PlainPreferences
import com.twofasapp.storage.PlainPreferencesImpl
import com.twofasapp.storage.internal.PreferencesDelegate
import com.twofasapp.storage.internal.EncryptedSharedPreferencesFactory
import com.twofasapp.storage.internal.PlainSharedPreferencesFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class PreferencesModule : KoinModule {

    override fun provide() = module {
        single<PlainPreferences> {
            val factory = PlainSharedPreferencesFactory(androidContext())
            PlainPreferencesImpl(delegate = PreferencesDelegate(factory = factory))
        }

        single<EncryptedPreferences> {
            val factory = EncryptedSharedPreferencesFactory(androidContext())
            EncryptedPreferencesImpl(delegate = PreferencesDelegate(factory = factory))
        }
    }
}