package com.twofasapp.feature.backup.ui.backupsettings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

internal class BackupSettingsViewModel : ViewModel() {

    val uiState: MutableStateFlow<BackupSettingsUiState> = MutableStateFlow(BackupSettingsUiState())

}