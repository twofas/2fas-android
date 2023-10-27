package com.twofasapp.data.services

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.Widget
import com.twofasapp.data.services.domain.Widgets
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.data.services.mapper.asEntity
import com.twofasapp.prefs.usecase.WidgetSettingsPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class WidgetsRepositoryImpl(
    private val servicesRepository: ServicesRepository,
    private val preference: WidgetSettingsPreference,
    private val timeProvider: TimeProvider,
) : WidgetsRepository {

    private val refreshTicker = MutableStateFlow(0L)

    override fun observeWidgets(): Flow<Widgets> {
        return combine(
            preference.flow(true),
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
        return preference.get()
            .asDomain(servicesRepository.observeServicesWithCode().first())
    }

    override suspend fun incrementLastInteraction() {
        val now = timeProvider.systemCurrentTime()

        refreshTicker.emit(now)

        preference.put { entity ->
            entity.copy(
                widgets = entity.widgets.map { it.copy(lastInteractionTimestamp = now) }
            )
        }
    }

    override suspend fun hideAll() {
        preference.put { entity ->
            entity.copy(
                widgets = entity.widgets.map { widgetEntity ->
                    widgetEntity.copy(
                        services = widgetEntity.services.map { service ->
                            service.copy(isActive = false)
                        }
                    )
                }
            )
        }
    }

    override suspend fun deleteWidget(appWidgetIds: List<Int>) {
        preference.put { entity ->
            entity.copy(
                widgets = entity.widgets.filterNot { appWidgetIds.contains(it.appWidgetId) }
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
        preference.put { entity ->
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
                            }.toMutableList()
                        )
                    } else {
                        widget
                    }
                }
            )
        }
    }

    override suspend fun generateHotpCode(serviceId: Long) {
        servicesRepository.incrementHotpCounter(servicesRepository.getService(serviceId))
    }

    override suspend fun saveWidget(widget: Widget) {
        preference.put { entity ->
            entity.copy(
                widgets = entity.widgets
                    .filter { it.appWidgetId != widget.appWidgetId }
                    .plus(widget.asEntity())
            )
        }
    }
}