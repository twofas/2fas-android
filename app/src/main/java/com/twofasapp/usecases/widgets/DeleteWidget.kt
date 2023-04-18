package com.twofasapp.usecases.widgets

import com.twofasapp.base.usecase.UseCaseParameterized
import io.reactivex.Scheduler

class DeleteWidget(
    private val widgetSettingsPreference: com.twofasapp.prefs.usecase.WidgetSettingsPreference,
) : UseCaseParameterized<Int, Unit> {

    override fun execute(params: Int, subscribeScheduler: Scheduler, observeScheduler: Scheduler) {
        widgetSettingsPreference.get().let { widgetSettings ->
            widgetSettingsPreference.put(
                widgetSettings.copy(widgets = widgetSettings.widgets.filter { it.appWidgetId != params })
            )
        }
    }
}