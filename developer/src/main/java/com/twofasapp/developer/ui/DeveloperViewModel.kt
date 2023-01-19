package com.twofasapp.developer.ui

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.developer.domain.ObserveLastPushesCase
import com.twofasapp.developer.domain.ObserveLastScannedQrCase
import com.twofasapp.featuretoggle.domain.ObserveFeatureTogglesCase
import com.twofasapp.featuretoggle.domain.UpdateFeatureEnabledCase
import com.twofasapp.featuretoggle.domain.model.FeatureToggle
import com.twofasapp.push.domain.GetFcmTokenCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DeveloperViewModel(
    private val dispatchers: Dispatchers,
    private val getFcmTokenCase: GetFcmTokenCase,
    private val observeFeatureTogglesCase: ObserveFeatureTogglesCase,
    private val updateFeatureEnabledCase: UpdateFeatureEnabledCase,
    private val observeLastScannedQrCase: ObserveLastScannedQrCase,
    private val observeLastPushesCase: ObserveLastPushesCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(DeveloperUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch(dispatchers.io()) { _uiState.update { it.copy(fcmToken = getFcmTokenCase()) } }

            launch {
                observeFeatureTogglesCase().flowOn(dispatchers.io()).collect { featureToggles ->
                    _uiState.update { it.copy(featureToggles = featureToggles) }
                }
            }

            launch {
                observeLastScannedQrCase().flowOn(dispatchers.io()).collect { lastScannedQr ->
                    _uiState.update { it.copy(lastScannedQr = lastScannedQr) }
                }
            }

            launch {
                observeLastPushesCase().flowOn(dispatchers.io()).collect { lastPushes ->
                    _uiState.update { it.copy(lastPushes = lastPushes) }
                }
            }
        }
    }

    fun onFeatureToggleChanged(featureToggle: FeatureToggle, isChecked: Boolean) {
        updateFeatureEnabledCase.execute(featureToggle, isChecked)
    }
}
