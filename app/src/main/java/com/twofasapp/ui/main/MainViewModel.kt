package com.twofasapp.ui.main

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.notifications.NotificationsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.feature.browserext.notification.DomainMatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update

internal class MainViewModel(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationsRepository: NotificationsRepository,
    private val browserExtRepository: BrowserExtRepository,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())

    init {
        launchScoped {
            sessionRepository.markAppInstalled()
        }

        launchScoped {
            val destination = when (sessionRepository.isOnboardingDisplayed()) {
                true -> MainUiState.StartDestination.Home
                false -> MainUiState.StartDestination.Onboarding
            }

            uiState.update { it.copy(startDestination = destination) }
        }

        launchScoped {
            settingsRepository.observeAppSettings()
                .distinctUntilChangedBy { it.selectedTheme }
                .collect { appSettings ->
                    uiState.update {
                        it.copy(selectedTheme = appSettings.selectedTheme)
                    }
                }

        }

        launchScoped {
            runSafely {
                notificationsRepository.fetchNotifications(
                    appInstallTimeMillis = sessionRepository.getAppInstallTimestamp(),
                    noCompanionAppFromTimeMillis = sessionRepository.noCompanionAppFromTimestamp(),
                )
            }
        }

        launchScoped {
            runSafely { browserExtRepository.fetchTokenRequests() }
        }

        launchScoped {
            browserExtRepository.observeTokenRequests().collect { requests ->
                uiState.update { state ->
                    state.copy(
                        browserExtRequests = requests.map { request ->
                            val domain = DomainMatcher.extractDomain(request.domain)
                            val matchedServices = DomainMatcher.findMatchingDomain(servicesRepository.getServices(), domain)

                            BrowserExtRequest(
                                request = request,
                                domain = domain,
                                matchedServices = matchedServices,
                            )
                        }
                            .distinctBy { it.request.requestId }
                    )
                }
            }
        }

        launchScoped {
            servicesRepository.observeAddServiceAdvancedExpanded().collect { expanded ->
                uiState.update { it.copy(addServiceAdvancedExpanded = expanded) }
            }
        }
    }

    fun browserExtRequestHandled(browserExtRequest: BrowserExtRequest) {
        launchScoped { browserExtRepository.deleteTokenRequest(browserExtRequest.request.requestId) }
    }

    fun serviceAdded(recentlyAddedService: RecentlyAddedService) {
        servicesRepository.pushRecentlyAddedService(recentlyAddedService)
    }

    fun toggleAdvanceExpanded() {
        launchScoped { servicesRepository.pushAddServiceAdvancedExpanded(uiState.value.addServiceAdvancedExpanded.not()) }
    }


    fun consumeEvent(event: MainUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    private fun publishEvent(event: MainUiEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
    }
}
