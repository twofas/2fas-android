package com.twofasapp

import com.twofasapp.base.ActivityProvider
import com.twofasapp.widgets.WidgetActionsImpl
import com.twofasapp.widgets.domain.WidgetActions
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val applicationModule = module {
    single { ActivityProvider() }

    singleOf(::WidgetActionsImpl) { bind<WidgetActions>() }
}