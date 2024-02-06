package com.twofasapp.feature.widget.ui.settings

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.WidgetsRepository
import com.twofasapp.data.services.domain.Widget
import com.twofasapp.data.services.domain.WidgetService
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.domain.ServicesSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class WidgetSettingsViewModel(
    private val widgetsRepository: WidgetsRepository,
    private val servicesRepository: ServicesRepository,
    private val settingsRepository: SettingsRepository,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    val uiState = MutableStateFlow(WidgetSettingsUiState())
    private val appWidgetIdState = MutableStateFlow(0)

    init {
        launchScoped {
            combine(servicesRepository.observeServices(), settingsRepository.observeAppSettings()) { services, appSettings -> services to appSettings }.collect { (services, appSettings) ->
                uiState.update {
                    it.copy(
                        loading = false,
                        services = services.sortedBy { service ->
                            when (appSettings.servicesSort) {
                                ServicesSort.Alphabetical -> service.name.lowercase()
                                ServicesSort.Manual -> null
                            }
                        },
                    )
                }
            }
        }

        launchScoped {
            appWidgetIdState.collect { appWidgetId ->
                uiState.update {
                    it.copy(
                        selected = widgetsRepository.getWidgets().list
                            .firstOrNull { widget -> widget.appWidgetId == appWidgetId }?.selectedServices.orEmpty()
                    )
                }
            }
        }
    }

    fun toggleService(serviceId: Long) {
        if (uiState.value.selected.contains(serviceId)) {
            uiState.update { it.copy(selected = it.selected.minus(serviceId)) }
        } else {
            uiState.update { it.copy(selected = it.selected.plus(serviceId)) }
        }
    }

    suspend fun save() {
        launchScoped {
            val widgets = widgetsRepository.getWidgets().list
            val widget = widgets.getOrNull(appWidgetIdState.value) ?: Widget(appWidgetId = appWidgetIdState.value)

            widgetsRepository.saveWidget(
                widget.copy(
                    lastInteraction = timeProvider.systemCurrentTime(),
                    services = uiState.value.services.filter { uiState.value.selected.contains(it.id) }
                        .map { service ->
                            WidgetService(
                                service = service,
                                revealed = false,
                            )
                        },
                )
            )
        }
    }

    fun updateAppWidgetId(appWidgetId: Int) {
        appWidgetIdState.update { appWidgetId }
    }
}