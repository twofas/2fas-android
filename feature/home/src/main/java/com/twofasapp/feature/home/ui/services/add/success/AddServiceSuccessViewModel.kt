package com.twofasapp.feature.home.ui.services.add.success

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.session.CustomizationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal class AddServiceSuccessViewModel(
    private val serviceId: Long,
    private val servicesRepository: ServicesRepository,
    private val customizationRepository: CustomizationRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<AddServiceSuccessUiState> = MutableStateFlow(AddServiceSuccessUiState())

    init {
        launchScoped {
            servicesRepository.observeServicesTicker()
                .map { services -> services.firstOrNull { it.id == serviceId } }
                .collect { service ->
                    uiState.update { it.copy(service = service) }
                }
        }

        launchScoped {
            combine(
                customizationRepository.observeShowNextCode(),
                customizationRepository.observeHideCodes(),
            ) { showNextCode, hideCodes -> showNextCode to hideCodes }
                .collect { (showNextCode, hideCodes) ->
                    uiState.update { it.copy(showNextCode = showNextCode, hideCodes = hideCodes) }
                }
        }
    }

    fun incrementHotpCounter() {
        val service = uiState.value.service ?: return
        launchScoped {
            servicesRepository.incrementHotpCounter(service)

            if (uiState.value.hideCodes) {
                servicesRepository.revealService(id = serviceId)
            }
        }
    }

    fun reveal() {
        launchScoped { servicesRepository.revealService(serviceId) }
    }
}