package com.twofasapp.widgets.configure

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.usecases.services.GetServices
import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.usecases.widgets.GetWidgetSettings
import com.twofasapp.usecases.widgets.UpdateWidget
import com.twofasapp.widgets.broadcast.WidgetBroadcaster
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy

class WidgetSettingsPresenter(
    private val view: WidgetSettingsContract.View,
    private val navigator: ScopedNavigator,
    private val getServices: GetServices,
    private val getWidgetSettings: GetWidgetSettings,
    private val updateWidget: UpdateWidget,
    private val widgetBroadcaster: WidgetBroadcaster,
    private val analyticsService: com.twofasapp.core.analytics.AnalyticsService,
    private val checkLockStatus: CheckLockStatus,
) : WidgetSettingsContract.Presenter() {

    private val checkedServiceIds = mutableListOf<Long>()
    private val services = mutableListOf<ServiceDto>()

    override fun onViewAttached() {
        view.toolbarBackClicks()
            .subscribeBy { view.close() }
            .addToDisposables()

        view.cancelClicks()
            .subscribeBy { view.close() }
            .addToDisposables()

        view.agreeClicks()
            .subscribeBy { view.showContent() }
            .addToDisposables()

        view.saveClicks()
            .subscribeBy {
                val existingWidget = getWidgetSettings.execute().getWidgetForId(view.getAppWidgetId())
                updateWidget.execute(
                    com.twofasapp.prefs.model.Widget(
                        appWidgetId = view.getAppWidgetId(),
                        lastInteractionTimestamp = existingWidget?.lastInteractionTimestamp
                            ?: 0,
                        services = checkedServiceIds.map { id ->
                            com.twofasapp.prefs.model.Widget.Service(
                                id = id,
                                isActive = existingWidget?.services?.find { it.id == id }?.isActive
                                    ?: false
                            )
                        }.toMutableList()
                    )
                )
                widgetBroadcaster.sendWidgetSettingsChanged()

                if (existingWidget == null) {
                    view.finishAddingWidget()
                } else {
                    view.close()
                }
            }
            .addToDisposables()

        getWidgetSettings.execute().widgets.find { it.appWidgetId == view.getAppWidgetId() }?.let {
            checkedServiceIds.addAll(it.services.map { service -> service.id })
        }

        if (view.isNew().not() || checkLockStatus.execute() == LockMethodEntity.NO_LOCK) {
            view.showContent()
        }

        refreshServices()
    }

    private fun refreshServices() {
        getServices.execute()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                services.clear()
                services.addAll(it)
                updateItems()
            }, { it.printStackTrace() })
            .addToDisposables()
    }

    private fun updateItems() {
        services.map {
            WidgetSettingsService(
                id = it.id,
                name = it.name,
                isChecked = checkedServiceIds.contains(it.id),
                switchAction = { entry, isChecked ->
                    if (isChecked) {
                        analyticsService.captureEvent(com.twofasapp.core.analytics.AnalyticsEvent.WIDGET_ADD_SERVICE)
                        checkedServiceIds.add(entry.id)
                    } else {
                        analyticsService.captureEvent(com.twofasapp.core.analytics.AnalyticsEvent.WIDGET_REMOVE_SERVICE)
                        checkedServiceIds.remove(entry.id)
                    }

                    updateItems()
                }
            ).toItem()
        }.let {
            view.setItems(listOf(WidgetSettingsHeaderItem()) + it)
        }
    }
}