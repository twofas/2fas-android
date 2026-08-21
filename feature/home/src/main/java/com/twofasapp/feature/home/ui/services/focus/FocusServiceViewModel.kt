package com.twofasapp.feature.home.ui.services.focus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.session.CustomizationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FocusServiceViewModel(
    savedStateHandle: SavedStateHandle,
    private val servicesRepository: ServicesRepository,
    private val customizationRepository: CustomizationRepository,
) : ViewModel() {

    private val serviceId: Long = savedStateHandle.getOrThrow(FocusServiceModalNavArg.ServiceId.name)
    internal val uiState: MutableStateFlow<FocusServiceUiState> = MutableStateFlow(FocusServiceUiState())

    init {
        launchScoped {
            servicesRepository.observeServicesTicker()
                .map { it.firstOrNull { it.id == serviceId } }
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
                    uiState.update {
                        it.copy(
                            showNextCode = showNextCode,
                            hideCodes = hideCodes,
                        )
                    }
                }
        }
    }

    fun incrementCounter() {
        uiState.value.service?.let {
            launchScoped {
                servicesRepository.incrementHotpCounter(it)

                if (uiState.value.hideCodes) {
                    servicesRepository.revealService(serviceId)
                }
            }
        }
    }

    fun reveal() {
        launchScoped { servicesRepository.revealService(serviceId) }
    }
}