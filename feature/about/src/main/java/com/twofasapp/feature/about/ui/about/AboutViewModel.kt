package com.twofasapp.feature.about.ui.about

import androidx.lifecycle.ViewModel
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.environment.BuildVariant
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class AboutViewModel(
    private val appBuild: AppBuild,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(AboutUiState())

    init {
        launchScoped {
            settingsRepository.observeSendCrashLogs().collect { sendCrashLogs ->
                uiState.update { it.copy(crashLogsEnabled = sendCrashLogs) }
            }
        }

        uiState.update {
            it.copy(
                version = buildString {
                    append("${appBuild.versionName} (${appBuild.versionCode})")

                    when (appBuild.buildVariant) {
                        BuildVariant.Release -> Unit
                        BuildVariant.Internal -> append(" - internal")
                        BuildVariant.Debug -> append(" - debug")
                    }
                },
            )
        }
    }

    fun toggleSendCrashLogs() {
        launchScoped {
            settingsRepository.setSendCrashLogs(uiState.value.crashLogsEnabled.not())
        }
    }
}