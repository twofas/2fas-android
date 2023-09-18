package com.twofasapp.usecases.widgets

import com.twofasapp.base.usecase.UseCase
import io.reactivex.Scheduler

class GetWidgetSettings(
    private val widgetSettingsPreference: com.twofasapp.prefs.usecase.WidgetSettingsPreference,
) : UseCase<com.twofasapp.prefs.model.WidgetSettingsEntity> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler): com.twofasapp.prefs.model.WidgetSettingsEntity {
        return widgetSettingsPreference.get()
    }
}