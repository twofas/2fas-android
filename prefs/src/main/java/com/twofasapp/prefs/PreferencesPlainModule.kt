package com.twofasapp.prefs

import com.twofasapp.common.di.KoinModule
import com.twofasapp.prefs.usecase.AppUpdateLastCheckVersionPreference
import com.twofasapp.prefs.usecase.CurrentAppVersionPreference
import com.twofasapp.prefs.usecase.GroupsPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.prefs.usecase.ServicesOrderPreference
import com.twofasapp.prefs.usecase.TimeDeltaPreference
import com.twofasapp.prefs.usecase.WidgetSettingsPreference
import com.twofasapp.storage.PlainPreferences
import org.koin.dsl.module

class PreferencesPlainModule : KoinModule {

    override fun provide() = module {
        single { TimeDeltaPreference(get<PlainPreferences>()) }
        single { RemoteBackupStatusPreference(get<PlainPreferences>()) }
        single { ServicesOrderPreference(get<PlainPreferences>()) }
        single { GroupsPreference(get<PlainPreferences>()) }
        single { WidgetSettingsPreference(get<PlainPreferences>()) }
        single { AppUpdateLastCheckVersionPreference(get<PlainPreferences>()) }
        single { CurrentAppVersionPreference(get<PlainPreferences>()) }
    }
}