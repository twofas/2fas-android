package com.twofasapp.prefs

import com.twofasapp.di.KoinModule
import com.twofasapp.prefs.usecase.AppUpdateLastCheckVersionPreference
import com.twofasapp.prefs.usecase.CacheValidityPreference
import com.twofasapp.prefs.usecase.CurrentAppVersionPreference
import com.twofasapp.prefs.usecase.DeveloperConfigPreference
import com.twofasapp.prefs.usecase.FirstCodeAddedPreference
import com.twofasapp.prefs.usecase.GroupsPreference
import com.twofasapp.prefs.usecase.LastPushesPreference
import com.twofasapp.prefs.usecase.LastScannedQrPreference
import com.twofasapp.prefs.usecase.LockMethodPreference
import com.twofasapp.prefs.usecase.MigratedToRoomPreference
import com.twofasapp.prefs.usecase.PinCodePreference
import com.twofasapp.prefs.usecase.RateAppStatusPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.prefs.usecase.ServicesOrderPreference
import com.twofasapp.prefs.usecase.ShowNextTokenPreference
import com.twofasapp.prefs.usecase.ShowOnboardingPreference
import com.twofasapp.prefs.usecase.TimeDeltaPreference
import com.twofasapp.prefs.usecase.WidgetSettingsPreference
import com.twofasapp.storage.PlainPreferences
import org.koin.dsl.module

class PreferencesPlainModule : KoinModule {

    override fun provide() = module {

        single { PinCodePreference(get<PlainPreferences>()) }
        single { DeveloperConfigPreference(get<PlainPreferences>()) }
        single { TimeDeltaPreference(get<PlainPreferences>()) }
        single { ShowNextTokenPreference(get<PlainPreferences>()) }
        single { LockMethodPreference(get<PlainPreferences>()) }
        single { ShowOnboardingPreference(get<PlainPreferences>()) }
        single { RateAppStatusPreference(get<PlainPreferences>()) }
        single { FirstCodeAddedPreference(get<PlainPreferences>()) }
        single { MigratedToRoomPreference(get<PlainPreferences>()) }
        single { LastScannedQrPreference(get<PlainPreferences>()) }
        single { RemoteBackupStatusPreference(get<PlainPreferences>()) }
        single { ServicesOrderPreference(get<PlainPreferences>()) }
        single { GroupsPreference(get<PlainPreferences>()) }
        single { WidgetSettingsPreference(get<PlainPreferences>()) }
        single { AppUpdateLastCheckVersionPreference(get<PlainPreferences>()) }
        single { CurrentAppVersionPreference(get<PlainPreferences>()) }
        single { LastPushesPreference(get<PlainPreferences>()) }
        single { CacheValidityPreference(get<PlainPreferences>(), get()) }
    }
}