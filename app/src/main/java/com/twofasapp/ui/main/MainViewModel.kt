package com.twofasapp.ui.main

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.notifications.NotificationsRepository
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update

internal class MainViewModel(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationsRepository: NotificationsRepository,
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

//        launchScoped {
//            uiState.update {
//                it.copy(selectedTheme = settingsRepository.getAppSettings().selectedTheme)
//            }
//        }

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
    }
}