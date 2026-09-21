package com.twofasapp.data.services

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.serializedPref
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.Widget
import com.twofasapp.data.services.domain.Widgets
import com.twofasapp.data.services.local.model.WidgetSettingsEntity
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.data.services.mapper.asEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class WidgetsRepositoryImpl(
    private val servicesRepository: ServicesRepository,
    private val timeProvider: TimeProvider,
    dataStoreOwner: DataStoreOwner,
) : WidgetsRepository, DataStoreOwner by dataStoreOwner {

    private val widgetSettings by serializedPref(
        name = "widgetSettings",
        default = WidgetSettingsEntity(),
        serializer = WidgetSettingsEntity.serializer(),
    )

    private val refreshTicker = MutableStateFlow(0L)

    override fun observeWidgets(): Flow<Widgets> {
        return combine(
            widgetSettings.asFlow(),
            servicesRepository.observeServicesWithCode(),
            refreshTicker,
        ) { a, b, c -> Triple(a, b, c) }.map { (widgets, _, _) ->
            widgets.asDomain(servicesRepository.observeServicesWithCode().first())
        }
    }

    override fun observeWidget(appWidgetId: Int): Flow<Widget> {
        return observeWidgets()
            .mapNotNull { it.list.firstOrNull { it.appWidgetId == appWidgetId } }
            .distinctUntilChanged()
    }

    override suspend fun getWidgets(): Widgets {
        return widgetSettings.get()
            .asDomain(servicesRepository.observeServicesWithCode().first())
    }

    override suspend fun incrementLastInteraction() {
        val now = timeProvider.systemCurrentTime()

        refreshTicker.emit(now)

        updateWidgetSettings { entity ->
            entity.copy(
                widgets = entity.widgets.map { it.copy(lastInteractionTimestamp = now) },
            )
        }
    }

    override suspend fun hideAll() {
        updateWidgetSettings { entity ->
            entity.copy(
                widgets = entity.widgets.map { widgetEntity ->
                    widgetEntity.copy(
                        services = widgetEntity.services.map { service ->
                            service.copy(isActive = false)
                        },
                    )
                },
            )
        }
    }

    override suspend fun deleteWidget(appWidgetIds: List<Int>) {
        updateWidgetSettings { entity ->
            entity.copy(
                widgets = entity.widgets.filterNot { appWidgetIds.contains(it.appWidgetId) },
            )
        }
    }

    override suspend fun incrementRefreshTicker() {
        refreshTicker.emit(timeProvider.systemCurrentTime())
    }

    override suspend fun getRefreshTicker(): Long {
        return refreshTicker.value
    }

    override suspend fun toggleService(appWidgetId: Int, serviceId: Long) {
        updateWidgetSettings { entity ->
            entity.copy(
                widgets = entity.widgets.map { widget ->
                    if (widget.appWidgetId == appWidgetId) {
                        widget.copy(
                            services = widget.services.map { service ->
                                if (service.id == serviceId) {
                                    service.copy(isActive = service.isActive.not())
                                } else {
                                    service
                                }
                            }.toMutableList(),
                        )
                    } else {
                        widget
                    }
                },
            )
        }
    }

    override suspend fun generateHotpCode(serviceId: Long) {
        servicesRepository.incrementHotpCounter(servicesRepository.getService(serviceId))
    }

    override suspend fun saveWidget(widget: Widget) {
        updateWidgetSettings { entity ->
            entity.copy(
                widgets = entity.widgets
                    .filter { it.appWidgetId != widget.appWidgetId }
                    .plus(widget.asEntity()),
            )
        }
    }

    private suspend fun updateWidgetSettings(action: (WidgetSettingsEntity) -> WidgetSettingsEntity) {
        widgetSettings.set(action(widgetSettings.get()))
    }
}