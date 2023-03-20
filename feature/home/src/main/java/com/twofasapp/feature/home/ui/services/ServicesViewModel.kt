package com.twofasapp.feature.home.ui.services

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.data.session.domain.ServicesSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

@Suppress("UNCHECKED_CAST")
internal class ServicesViewModel(
    private val servicesRepository: ServicesRepository,
    private val groupsRepository: GroupsRepository,
    private val settingsRepository: SettingsRepository,
    private val sessionRepository: SessionRepository,
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
                isInEditMode,
                settingsRepository.observeAppSettings(),
                sessionRepository.observeShowBackupReminder(),
                sessionRepository.observeBackupEnabled(),
                searchQuery,
            ) { array ->
                CombinedResult(
                    groups = array[0] as List<Group>,
                    services = array[1] as List<Service>,
                    isInEditMode = array[2] as Boolean,
                    appSettings = array[3] as AppSettings,
                    showBackupReminder = array[4] as Boolean,
                    backupEnabled = array[5] as Boolean,
                    searchQuery = array[6] as String,
                )
            }.collect { result ->

                val showSyncReminder = result.appSettings.showBackupNotice && result.showBackupReminder && result.backupEnabled.not()
                val showSyncNoticeBar = result.appSettings.showBackupNotice && showSyncReminder.not() && result.backupEnabled.not()

//                servicesRepository.setTickerEnabled(result.isInEditMode.not())

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
                        showSyncNoticeBar = showSyncNoticeBar,
                        showSyncReminder = showSyncReminder,
                        totalGroups = result.groups.size,
                        totalServices = result.services.size,
                        isLoading = false,
                        isInEditMode = result.isInEditMode,
                        appSettings = result.appSettings,
                        items = buildList {
                            result.groups.forEach { group ->
                                if (showSyncNoticeBar) {
                                    add(ServicesListItem.SyncNoticeBar)
                                }

                                if (showSyncReminder) {
                                    add(ServicesListItem.SyncReminder)
                                }

                                add(ServicesListItem.Group(group.id))

                                result.services.filter { it.groupId == group.id }.forEach { service ->
                                    add(ServicesListItem.Service(service.id))
                                }
                            }
                        }
                    )
                }
            }
        }

        launchScoped {
            servicesRepository.observeRecentlyAddedService().collect {
                publishEvent(ServicesStateEvent.ShowServiceAddedModal(it.id))
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

    fun swapServices(from: Int, to: Int) {
        var items = uiState.value.items.toMutableList()
        val fromItem = items[from]
        val toItem = items[to]

        if (fromItem is ServicesListItem.Service && toItem is ServicesListItem.Service) {
            // Swap items
            items = items.apply { add(to, removeAt(from)) }

            servicesRepository.updateServicesOrder(
                ids = items.filterIsInstance<ServicesListItem.Service>().map { it.id }
            )
        }

        if (fromItem is ServicesListItem.Service && toItem is ServicesListItem.Group) {
            val groupId = if (from < to) {
                toItem.id
            } else {
                items.subList(0, to)
                    .filterIsInstance<ServicesListItem.Group>()
                    .asReversed()
                    .firstOrNull()?.id
            }

            launchScoped { servicesRepository.setServiceGroup(fromItem.id, groupId) }
        }
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

    fun dismissSyncReminder() {
        launchScoped {
            sessionRepository.resetBackupReminder()
        }
    }

    fun incrementHotpCounter(service: Service) {
        launchScoped { servicesRepository.incrementHotpCounter(service) }
    }

    private fun publishEvent(event: ServicesStateEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
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
        val showBackupReminder: Boolean,
        val backupEnabled: Boolean,
        val searchQuery: String,
    )
}