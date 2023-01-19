package com.twofasapp.usecases.widgets

import com.twofasapp.prefs.usecase.WidgetSettingsPreference
import com.twofasapp.base.usecase.UseCase
import io.reactivex.Scheduler

class DeactivateAllWidgets(
    private val widgetSettingsPreference: com.twofasapp.prefs.usecase.WidgetSettingsPreference,
) : UseCase<Unit> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler) {
        widgetSettingsPreference.get().let { widgetSettings ->
            widgetSettingsPreference.put(
                widgetSettings.copy(
                    widgets = widgetSettings.widgets.map { widget ->
                        widget.copy(
                            lastInteractionTimestamp = 0,
                            services = widget.services.map { it.copy(isActive = false) }.toMutableList()
                        )
                    }
                )
            )
        }
    }
}