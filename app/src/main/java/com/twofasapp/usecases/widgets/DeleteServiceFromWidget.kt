package com.twofasapp.usecases.widgets

import com.twofasapp.prefs.usecase.WidgetSettingsPreference
import com.twofasapp.base.usecase.UseCaseParameterized
import io.reactivex.Scheduler

class DeleteServiceFromWidget(
    private val widgetSettingsPreference: com.twofasapp.prefs.usecase.WidgetSettingsPreference,
) : UseCaseParameterized<Long, Unit> {

    override fun execute(params: Long, subscribeScheduler: Scheduler, observeScheduler: Scheduler) {
        widgetSettingsPreference.get().let { widgetSettings ->
            widgetSettingsPreference.put(
                widgetSettings.copy(widgets = widgetSettings.widgets.map {
                    it.copy(services = it.services.filter { service -> service.id != params }.toMutableList())
                })
            )
        }
    }
}