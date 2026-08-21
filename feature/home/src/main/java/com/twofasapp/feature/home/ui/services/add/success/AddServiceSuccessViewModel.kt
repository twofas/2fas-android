package com.twofasapp.feature.home.ui.services.add.success

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.common.domain.Service
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.session.CustomizationRepository
import com.twofasapp.feature.home.ui.services.add.NavArg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class AddServiceSuccessViewModel(
    savedStateHandle: SavedStateHandle,
    private val servicesRepository: ServicesRepository,
    private val customizationRepository: CustomizationRepository,
) : ViewModel() {

    private val serviceId: Long = savedStateHandle.getOrThrow(NavArg.ServiceId.name)

    val uiState: MutableStateFlow<AddServiceSuccessUiState> = MutableStateFlow(AddServiceSuccessUiState())

    init {
        launchScoped {
            servicesRepository.observeServicesTicker().collect { services ->
                uiState.update { state ->
                    state.copy(
                        service = services.firstOrNull { it.id == serviceId },
                    )
                }
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

    fun incrementHotpCounter(service: Service) {
        launchScoped {
            servicesRepository.incrementHotpCounter(service)

            if (uiState.value.hideCodes) {
                servicesRepository.revealService(id = service.id)
            }
        }
    }

    fun reveal(service: Service) {
        launchScoped { servicesRepository.revealService(service.id) }
    }
}