package com.twofasapp.data.services

import com.twofasapp.data.services.domain.Widgets
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.prefs.usecase.WidgetSettingsPreference
import kotlinx.coroutines.flow.first

class WidgetsRepositoryImpl(
    private val servicesRepository: ServicesRepository,
    private val widgetSettingsPreference: WidgetSettingsPreference,
) : WidgetsRepository {

    override suspend fun getWidgets(): Widgets {
        return widgetSettingsPreference.get()
            .asDomain(servicesRepository.observeServicesWithCode().first())
    }

    override suspend fun toggleService(serviceId: Long) {
        widgetSettingsPreference.put {entity ->
            entity.copy(
               widgets =  entity.widgets.map { widget ->
                   widget.copy(
                       services = widget.services.map { service ->
                           if (service.id == serviceId) {
                               service.copy(isActive = service.isActive.not())
                           } else {
                               service
                           }
                       }.toMutableList()
                   )
               }
           )
        }
    }
}