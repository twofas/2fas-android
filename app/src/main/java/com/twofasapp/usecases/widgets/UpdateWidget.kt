package com.twofasapp.usecases.widgets

import com.twofasapp.base.usecase.UseCaseParameterized
import io.reactivex.Scheduler

class UpdateWidget(
    private val widgetSettingsPreference: com.twofasapp.prefs.usecase.WidgetSettingsPreference,
) : UseCaseParameterized<com.twofasapp.prefs.model.WidgetEntity, Unit> {

    override fun execute(params: com.twofasapp.prefs.model.WidgetEntity, subscribeScheduler: Scheduler, observeScheduler: Scheduler) {
        widgetSettingsPreference.get().let { widgetSettings ->
            widgetSettingsPreference.put(
                widgetSettings.copy(
                    widgets = widgetSettings.widgets
                        .filter { it.appWidgetId != params.appWidgetId }
                        .plus(params)
                )
            )
        }
    }
}