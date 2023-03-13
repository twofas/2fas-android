package com.twofasapp.feature.trash.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.Service
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TrashViewModel(
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    val services: StateFlow<List<Service>> =
        servicesRepository.observeDeletedServices()
            .map { list -> list.sortedByDescending { it.id } } // TODO: Sort by time
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun restoreService(id: Long) {
        launchScoped {
            servicesRepository.restoreService(id)
        }
    }
}