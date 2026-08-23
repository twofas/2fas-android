package com.twofasapp.feature.trash.ui.dispose

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DisposeViewModel(
    private val serviceId: Long,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(DisposeUiState(""))

    init {
        launchScoped {
            uiState.update {
                DisposeUiState(
                    serviceName = servicesRepository.getService(serviceId).name,
                )
            }
        }
    }

    fun delete() {
        launchScoped {
            servicesRepository.deleteService(serviceId)
            // See: DeleteServiceUseCase
        }
    }
}