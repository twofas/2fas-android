package com.twofasapp.storage.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.storage.PlainPreferences
import com.twofasapp.storage.PlainPreferencesImpl
import com.twofasapp.storage.internal.PlainSharedPreferencesFactory
import com.twofasapp.storage.internal.PreferencesDelegate
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class PrefsModule : KoinModule {

    override fun provide() = module {
        single<PlainPreferences> {
            val factory = PlainSharedPreferencesFactory(androidContext())
            PlainPreferencesImpl(delegate = PreferencesDelegate(factory = factory))
        }
    }
}