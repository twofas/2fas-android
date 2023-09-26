package com.twofasapp.ui.main

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.twofasapp.DeeplinkHandler
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.notifications.NotificationsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.services.otp.OtpLinkParser
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
    private val deeplinkHandler: DeeplinkHandler,
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
            runSafely { notificationsRepository.fetchNotifications(sessionRepository.getAppInstallTimestamp()) }
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
            deeplinkHandler.observeQueuedDeeplink().collect {
                handleIncomingData(it)
                deeplinkHandler.setQueuedDeeplink(null)
            }
        }

        launchScoped {
            servicesRepository.observeAddServiceAdvancedExpanded().collect { expanded ->
                uiState.update { it.copy(addServiceAdvancedExpanded = expanded) }
            }
        }
    }

    @SuppressLint("CheckResult")
    fun handleIncomingData(incomingData: String?) {
        if (incomingData == null) return
        println("dupa: $incomingData")
        launchScoped {

            if (incomingData.startsWith("content://") && incomingData.endsWith(".2fas")) {
                // Import backup
                // dupa: content://com.metago.astro.filecontent/file/storage/emulated/0/Download/2fas-backup-20230926134738.2fas


            }

            if (incomingData.startsWith("otpauth")) {
                val otpLink = OtpLinkParser.parse(incomingData)
                otpLink?.let {
                    if (servicesRepository.isServiceValid(otpLink).not()) {
                        return@launchScoped
                    }

                    val id = servicesRepository.addService(otpLink)
                    servicesRepository.pushRecentlyAddedService(
                        RecentlyAddedService(
                            serviceId = id, source = RecentlyAddedService.Source.Manually
                        )
                    )
                }
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
}
