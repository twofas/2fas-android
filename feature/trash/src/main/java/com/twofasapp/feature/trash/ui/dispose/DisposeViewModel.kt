package com.twofasapp.feature.trash.ui.dispose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.navigation.errorNavArg
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.feature.trash.navigation.NavArg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DisposeViewModel(
    savedStateHandle: SavedStateHandle,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    private val serviceId: Long = savedStateHandle[NavArg.ServiceId.name] ?: errorNavArg(NavArg.ServiceId)

    val uiState = MutableStateFlow(DisposeUiState(""))

    init {
        launchScoped {
            uiState.update {
                DisposeUiState(
                    serviceName = servicesRepository.getService(serviceId).name
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