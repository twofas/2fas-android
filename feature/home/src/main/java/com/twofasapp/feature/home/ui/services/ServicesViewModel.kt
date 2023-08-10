package com.twofasapp.feature.home.ui.services

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.data.session.domain.ServicesSort
import kotlinx.coroutines.Dispatchers
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

                val filteredServices = result.services
                    .sortedBy {
                        when (result.appSettings.servicesSort) {
                            ServicesSort.Alphabetical -> it.name.lowercase()
                            ServicesSort.Manual -> null
                        }
                    }
                    .filter { service -> service.isMatchingQuery(result.searchQuery) }

                uiState.update { state ->
                    state.copy(
                        services = filteredServices,
                        groups = result.groups,
                        showSyncNoticeBar = showSyncNoticeBar,
                        showSyncReminder = showSyncReminder,
                        totalGroups = result.groups.size,
                        totalServices = result.services.size,
                        isLoading = false,
                        isInEditMode = result.isInEditMode,
                        appSettings = result.appSettings,
                        items = buildList {

                            if (showSyncNoticeBar) {
                                add(ServicesListItem.SyncNoticeBar)
                            }

                            if (showSyncReminder) {
                                add(ServicesListItem.SyncReminder)
                            }

                            val groupedServices: Map<Group, List<Service>> = buildMap {
                                result.groups.forEach { group ->
                                    put(
                                        key = group,
                                        value = filteredServices
                                            .filter { it.groupId == group.id }
                                    )
                                }
                            }

                            groupedServices.forEach { (group, services) ->

                                if (groupedServices.size > 1) {
                                    if (group.id != null || services.isNotEmpty() || result.searchQuery.isNotEmpty()) {
                                        val group = result.groups.first { it.id == group.id }
                                        add(
                                            ServicesListItem.GroupItem(
                                                if (result.searchQuery.isNotEmpty()) {
                                                    group.copy(isExpanded = true)
                                                } else {
                                                    group
                                                }
                                            )
                                        )
                                    }
                                }

                                if (group.isExpanded || result.isInEditMode || groupedServices.size == 1 || result.searchQuery.isNotEmpty()) {
                                    services.forEach { service ->
                                        add(ServicesListItem.ServiceItem(service))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        launchScoped {
            servicesRepository.observeRecentlyAddedService().collect { recentlyAdded ->
                if (recentlyAdded.source == RecentlyAddedService.Source.QrGallery) {
                    publishEvent(ServicesStateEvent.ShowQrFromGalleryDialog)
                }
                publishEvent(ServicesStateEvent.ServiceAdded(recentlyAdded.serviceId))
            }
        }
    }

    fun toggleEditMode() {
        isInEditMode.value = isInEditMode.value.not()
    }

    fun consumeEvent(event: ServicesStateEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    fun search(query: String) {
        searchQuery.update { query }
        uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleGroup(id: String?) {
        if (uiState.value.searchQuery.isNotEmpty()) return

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
        if (uiState.value.searchFocused == focused) return

        uiState.update { it.copy(searchFocused = focused) }
    }

    fun dismissSyncReminder() {
        launchScoped {
            sessionRepository.resetBackupReminder()
        }
    }

    fun incrementHotpCounter(service: Service) {
        launchScoped {
            servicesRepository.incrementHotpCounter(service)

            if (uiState.value.appSettings.hideCodes) {
                servicesRepository.revealService(id = service.id)
            }
        }
    }

    private fun publishEvent(event: ServicesStateEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
    }

    private fun Service.isMatchingQuery(query: String): Boolean {
        return name.contains(query, true) ||
                issuer?.contains(query, true) ?: false ||
                info?.contains(query, true) ?: false ||
                tags.contains(query.lowercase())
    }

    fun onDragStart() {
        println("onDragStart")
        servicesRepository.setTickerEnabled(false)
    }

    fun onDragEnd(data: List<ServicesListItem>) {
        println("onDragEnd")
        launchScoped(Dispatchers.IO) {
            var groupId: String? = null

            data.forEach { item ->
                if (item is ServicesListItem.GroupItem) {
                    groupId = item.group.id
                }

                if (item is ServicesListItem.ServiceItem && item.service.groupId != groupId) {
                    servicesRepository.setServiceGroup(item.service.id, groupId)
                }
            }

            servicesRepository.updateServicesOrder(
                ids = data.filterIsInstance<ServicesListItem.ServiceItem>().map { it.service.id }
            )

            servicesRepository.setTickerEnabled(true)
        }
    }

    fun reveal(service: Service) {
        launchScoped {
            servicesRepository.revealService(
                id = service.id,
            )
        }
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