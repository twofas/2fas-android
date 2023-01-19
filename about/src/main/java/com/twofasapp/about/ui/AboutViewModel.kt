package com.twofasapp.about.ui

import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.environment.AppConfig
import com.twofasapp.environment.BuildVariant
import com.twofasapp.prefs.usecase.RateAppStatusPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant

internal class AboutViewModel(
    private val dispatchers: Dispatchers,
    private val appConfig: AppConfig,
    private val rateAppStatusPreference: RateAppStatusPreference

) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(versionName = generateVersionName())
        }
    }

    private fun generateVersionName(): String =
        if (appConfig.buildVariant != BuildVariant.Release) {
            "${appConfig.versionName} (${appConfig.buildVariant.name})"
        } else {
            appConfig.versionName
        }

    fun reviewDone() {
        rateAppStatusPreference.put(
            rateAppStatusPreference.get().copy(counterStarted = Instant.now(), counterReached = Instant.now())
        )
    }
}