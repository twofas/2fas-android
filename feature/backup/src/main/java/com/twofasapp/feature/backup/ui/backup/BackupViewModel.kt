package com.twofasapp.feature.backup.ui.backup

import androidx.lifecycle.ViewModel
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class BackupViewModel(
    private val appBuild: AppBuild,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<BackupUiState> = MutableStateFlow(BackupUiState())

    init {
        launchScoped {
            uiState.update { it.copy(debuggable = appBuild.debuggable) }
        }

        launchScoped {
            uiState.update { it.copy(exportEnabled = servicesRepository.getServices().isNotEmpty()) }
        }
    }
}