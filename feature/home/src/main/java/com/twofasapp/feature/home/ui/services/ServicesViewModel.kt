package com.twofasapp.feature.home.ui.services

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.data.session.domain.ServicesSort
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
    private val searchQuery = MutableStateFlow("")

    init {
        searchFocused(settingsRepository.getAppSettings().autoFocusSearch)

        launchScoped {
            combine(
                groupsRepository.observeGroups(),
                servicesRepository.observeServicesTicker(),
                settingsRepository.observeAppSettings(),
                isInEditMode,
                searchQuery,
            ) { groups, services, appSettings, isInEditMode, query -> CombinedResult(groups, services, isInEditMode, appSettings, query) }.collect { result ->


                uiState.update { state ->
                    state.copy(
                        groups = if (result.searchQuery.isEmpty()) {
                            result.groups
                        } else {
                            result.groups.map { it.copy(isExpanded = true) }
                        },
                        services = result.services
                            .sortedBy {
                                when (result.appSettings.servicesSort) {
                                    ServicesSort.Alphabetical -> it.name.lowercase()
                                    ServicesSort.Manual -> null
                                }
                            }
                            .filter { service -> service.isMatchingQuery(result.searchQuery) },
                        isLoading = false,
                        isInEditMode = result.isInEditMode,
                        appSettings = result.appSettings
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

    fun search(query: String) {
        searchQuery.update { query }
        uiState.update { it.copy(searchQuery = query) }
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

    fun updateSort(index: Int) {
        launchScoped {
            settingsRepository.setServicesSort(
                when (index) {
                    0 -> ServicesSort.Alphabetical
                    else -> ServicesSort.Manual
                }
            )
        }
    }

    fun searchFocused(focused: Boolean) {
        uiState.update { it.copy(searchFocused = focused) }
    }

    private fun Service.isMatchingQuery(query: String): Boolean {
        return name.contains(query, true) ||
                info?.contains(query, true) ?: false ||
                tags.contains(query.lowercase())
    }

    data class CombinedResult(
        val groups: List<Group>,
        val services: List<Service>,
        val isInEditMode: Boolean,
        val appSettings: AppSettings,
        val searchQuery: String,
    )
}