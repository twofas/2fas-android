package com.twofasapp.feature.home.ui.services.add.success

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.feature.home.ui.services.add.NavArg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class AddServiceSuccessViewModel(
    savedStateHandle: SavedStateHandle,
    private val servicesRepository: ServicesRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val serviceId: Long = savedStateHandle.getOrThrow(NavArg.ServiceId.name)

    val uiState: MutableStateFlow<AddServiceSuccessUiState> = MutableStateFlow(AddServiceSuccessUiState())

    init {
        launchScoped {
            servicesRepository.observeServicesTicker().collect { services ->
                uiState.update { state ->
                    state.copy(
                        service = services.firstOrNull { it.id == serviceId }
                    )
                }
            }
        }

        launchScoped {
            settingsRepository.observeAppSettings()
                .collect { settings ->
                    uiState.update {
                        it.copy(
                            showNextCode = settings.showNextCode,
                            hideCodes = settings.hideCodes,
                        )
                    }
                }
        }
    }

    fun incrementHotpCounter(service: Service) {
        launchScoped { servicesRepository.incrementHotpCounter(service) }
    }

    fun reveal(service: Service) {
        launchScoped { servicesRepository.revealService(service.id) }
    }
}
