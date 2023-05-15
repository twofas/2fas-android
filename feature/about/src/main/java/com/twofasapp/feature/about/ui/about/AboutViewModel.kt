package com.twofasapp.feature.about.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.environment.BuildVariant
import com.twofasapp.common.ktx.camelCaseBeginLower
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AboutViewModel(
    private val dispatchers: Dispatchers,
    private val appBuild: AppBuild,
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(AboutUiState())

    init {
        launchScoped {
            settingsRepository.observeAppSettings()
                .collect { appSettings ->
                    uiState.update { it.copy(appSettings = appSettings) }
                }
        }
    }

    val versionName =
        if (appBuild.buildVariant != BuildVariant.Release) {
            "${appBuild.versionName} (${appBuild.buildVariant.name.camelCaseBeginLower()})"
        } else {
            appBuild.versionName
        }

    fun reviewDone() {
        viewModelScope.launch(dispatchers.io) {
            sessionRepository.setRateAppDisplayed(true)
        }
        // TODO
//        rateAppStatusPreference.put(
//            rateAppStatusPreference.get().copy(counterStarted = Instant.now(), counterReached = Instant.now())
//        )
    }

    fun toggleSendCrashLogs() {
        launchScoped {
            settingsRepository.setSendCrashLogs(uiState.value.appSettings.sendCrashLogs.not())
        }
    }
}