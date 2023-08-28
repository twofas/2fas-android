package com.twofasapp.feature.backup.ui.backup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

internal class BackupViewModel : ViewModel() {

    val uiState: MutableStateFlow<BackupUiState> = MutableStateFlow(BackupUiState())

}