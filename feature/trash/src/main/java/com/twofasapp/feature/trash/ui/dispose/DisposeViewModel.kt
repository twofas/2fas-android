package com.twofasapp.feature.trash.ui.dispose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DisposeViewModel(
    savedStateHandle: SavedStateHandle,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    private val serviceId: Long = savedStateHandle.getOrThrow(NavArg.ServiceId.name)

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