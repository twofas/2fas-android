package com.twofasapp.feature.trash.ui.trash

import androidx.lifecycle.ViewModel
import com.twofasapp.common.domain.Service
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.core.design.state.ScreenState
import com.twofasapp.core.design.state.empty
import com.twofasapp.core.design.state.loading
import com.twofasapp.core.design.state.success
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.locale.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class TrashViewModel(
    private val strings: Strings,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {
    val uiState = MutableStateFlow(TrashUiState())
    val screenState = MutableStateFlow(ScreenState.Loading)

    init {
        launchScoped {
            servicesRepository.observeDeletedServices().collect { services ->
                val sorted = services.sortedByDescending { it.updatedAt }

                uiState.update { it.copy(trashedItems = sorted) }

                if (sorted.isEmpty()) {
                    screenState.empty(strings.trashEmpty)
                } else {
                    screenState.success()
                }
            }
        }
    }

    fun restore(onComplete: (String) -> Unit) {
        launchScoped {
            val ids = uiState.value.selected
            screenState.loading()
            clearSelections()
            ids.forEach { servicesRepository.restoreService(it) }
        }.invokeOnCompletion { onComplete("Items restored!") }
    }

    fun delete(onComplete: (String) -> Unit) {
        launchScoped {
            val ids = uiState.value.selected
            screenState.loading()
            clearSelections()
            ids.forEach { servicesRepository.deleteService(it) }
        }.invokeOnCompletion { onComplete("Items deleted!") }
    }

    fun toggle(service: Service) {
        if (uiState.value.selected.contains(service.id)) {
            uiState.update { it.copy(selected = it.selected.minus(service.id)) }
        } else {
            uiState.update { it.copy(selected = it.selected.plus(service.id)) }
        }
    }

    fun selectAll() {
        uiState.update { it.copy(selected = it.trashedItems.map { service -> service.id }) }
    }

    fun clearSelections() {
        uiState.update { it.copy(selected = emptyList()) }
    }
}