package com.twofasapp.usecases.widgets

import com.twofasapp.prefs.model.WidgetSettings
import com.twofasapp.prefs.usecase.WidgetSettingsPreference
import com.twofasapp.base.usecase.UseCase
import io.reactivex.Scheduler

class GetWidgetSettings(
    private val widgetSettingsPreference: com.twofasapp.prefs.usecase.WidgetSettingsPreference,
) : UseCase<com.twofasapp.prefs.model.WidgetSettings> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler): com.twofasapp.prefs.model.WidgetSettings {
        return widgetSettingsPreference.get()
    }
}