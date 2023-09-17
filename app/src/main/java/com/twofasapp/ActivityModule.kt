package com.twofasapp

import com.twofasapp.features.navigator.ActivityScopedNavigator
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.widgets.configure.WidgetSettingsActivity
import com.twofasapp.widgets.configure.WidgetSettingsContract
import com.twofasapp.widgets.configure.WidgetSettingsPresenter
import org.koin.androidx.scope.ScopeActivity
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module

inline fun <reified T : ScopeActivity> Module.activityScope(scopeSet: ScopeDSL.() -> Unit) {
    scope<T> {
        scoped<ScopedNavigator> { ActivityScopedNavigator(get(), get()) }
        scopeSet()
    }
}

val activityScopeModule = module {

    factory<ScopedNavigator> { ActivityScopedNavigator(get(), get()) }

    activityScope<WidgetSettingsActivity> {
        scopedOf(::WidgetSettingsPresenter) { bind<WidgetSettingsContract.Presenter>() }
    }
}