package com.twofasapp.feature.home.ui.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ServicesViewModel(
    private val servicesRepository: ServicesRepository,
    private val groupsRepository: GroupsRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(ServicesUiState())
//    val orderList = MutableStateFlow(listOf(1, 2, 3, 4, 5, 6))

    private val isInEditMode = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            combine(
                servicesRepository.observeServicesTicker(),
                groupsRepository.observeGroups(),
                isInEditMode,
            ) { services, groups, isInEditMode -> CombinedResult(services, groups, isInEditMode) }.collect { result ->

                uiState.update {
                    it.copy(
                        services = result.services,
                        isLoading = false,
                        isInEditMode = result.isInEditMode,
                    )
                }
            }
        }
    }

    fun toggleEditMode() {
        isInEditMode.value = isInEditMode.value.not()
    }

    fun toggleAddMenu() {
        uiState.update { it.copy(events = it.events.plus(ServicesStateEvent.ShowAddServiceModal)) }
    }

    fun consumeEvent(event: ServicesStateEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    data class CombinedResult(
        val services: List<Service>,
        val groups: List<Group>,
        val isInEditMode: Boolean,
    )
}