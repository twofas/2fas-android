package com.twofasapp.feature.home.ui.services

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.session.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class ServicesViewModel(
    private val servicesRepository: ServicesRepository,
    private val groupsRepository: GroupsRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(ServicesUiState())
//    val orderList = MutableStateFlow(listOf(1, 2, 3, 4, 5, 6))

    private val isInEditMode = MutableStateFlow(false)

    init {
        launchScoped {
            combine(
                groupsRepository.observeGroups(),
                servicesRepository.observeServicesTicker(),
                isInEditMode,
            ) { groups, services, isInEditMode -> CombinedResult(groups, services, isInEditMode) }.collect { result ->

                uiState.update {
                    it.copy(
                        groups = result.groups,
                        services = result.services,
                        isLoading = false,
                        isInEditMode = result.isInEditMode,
                    )
                }
            }
        }

        launchScoped {
            settingsRepository.observeAppSettings()
                .collect { appSettings -> uiState.update { it.copy(appSettings = appSettings) } }
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

    fun search(query: String) {

    }

    fun toggleGroup(id: String?) {
        launchScoped { groupsRepository.toggleGroup(id) }
    }

    fun addGroup(name: String) {
        launchScoped { groupsRepository.addGroup(name) }
    }

    fun deleteGroup(id: String) {
        launchScoped { groupsRepository.deleteGroup(id) }
    }

    fun editGroup(id: String, name: String) {
        launchScoped { groupsRepository.editGroup(id, name) }
    }

    fun moveUpGroup(id: String) {
        launchScoped { groupsRepository.moveUpGroup(id) }
    }

    fun moveDownGroup(id: String) {
        launchScoped { groupsRepository.moveDownGroup(id) }
    }

    fun swapServices(from: Long, to: Long) {
        launchScoped { servicesRepository.swapServices(from, to) }
    }

    data class CombinedResult(
        val groups: List<Group>,
        val services: List<Service>,
        val isInEditMode: Boolean,
    )
}