package com.twofasapp.feature.widget.ui.configure

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.WidgetsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class WidgetSetupViewModel(
    private val widgetsRepository: WidgetsRepository,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(WidgetSetupUiState())

    init {
        launchScoped {
            servicesRepository.observeServices().collect { services ->
                uiState.update { it.copy(services = services) }
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

    fun save() {
        launchScoped {

            uiState.update { it.copy(finishSuccess = true) }
        }
    }
}