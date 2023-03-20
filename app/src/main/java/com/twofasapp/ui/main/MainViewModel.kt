package com.twofasapp.ui.main

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.twofasapp.browserextension.notification.DomainMatcher
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.notifications.NotificationsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.services.domain.ConvertOtpLinkToService
import com.twofasapp.start.domain.DeeplinkHandler
import com.twofasapp.usecases.services.AddService
import com.twofasapp.usecases.services.CheckServiceExists
import com.twofasapp.usecases.totp.ParseOtpAuthLink
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update

internal class MainViewModel(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationsRepository: NotificationsRepository,
    private val browserExtRepository: BrowserExtRepository,
    private val servicesRepository: ServicesRepository,
    private val parseOtpAuthLink: ParseOtpAuthLink,
    private val checkServiceExists: CheckServiceExists,
    private val convertOtpToServiceCase: ConvertOtpLinkToService,
    private val addService: AddService,
    private val deeplinkHandler: DeeplinkHandler,
) : ViewModel() {

    val uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())

    init {
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
            runSafely { notificationsRepository.fetchNotifications() }
        }

        launchScoped {
            runSafely { browserExtRepository.fetchTokenRequests() }
                .onSuccess { requests ->

                    uiState.update { state ->
                        state.copy(
                            browserExtRequests = state.browserExtRequests.plus(requests.map { request ->
                                val domain = DomainMatcher.extractDomain(request.domain)
                                val matchedServices = DomainMatcher.findServicesMatchingDomainNew(servicesRepository.getServices(), domain)

                                BrowserExtRequest(
                                    request = request,
                                    domain = domain,
                                    matchedServices = matchedServices,
                                )
                            }
                            ).distinctBy { it.request.requestId }
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
    }

    @SuppressLint("CheckResult")
    fun handleIncomingData(incomingData: String?) {
        if (incomingData != null) {
            parseOtpAuthLink.execute(ParseOtpAuthLink.Params(incomingData))
                .flatMap { checkServiceExists.execute(it.secret) }
                .subscribeBy(
                    onSuccess = { isExists ->
                        if (isExists.not()) {
                            parseOtpAuthLink.execute(ParseOtpAuthLink.Params(incomingData))
                                .map { convertOtpToServiceCase.execute(it) }
                                .flatMapCompletable { addService.execute(AddService.Params(it)) }
                                .subscribe({ }, {})
                        }
                    },
                    onError = {}
                )
        }
    }
}
