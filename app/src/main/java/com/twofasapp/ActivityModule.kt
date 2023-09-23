package com.twofasapp

import com.twofasapp.features.navigator.ActivityScopedNavigator
import com.twofasapp.prefs.ScopedNavigator
import org.koin.dsl.module

val activityScopeModule = module {

    factory<ScopedNavigator> { ActivityScopedNavigator(get(), get()) }

}