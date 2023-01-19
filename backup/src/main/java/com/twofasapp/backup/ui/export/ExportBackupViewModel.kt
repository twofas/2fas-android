package com.twofasapp.backup.ui.export

import com.twofasapp.backup.data.FilesProvider
import com.twofasapp.backup.domain.ExportBackupSuspended
import com.twofasapp.base.BaseViewModel
import com.twofasapp.prefs.model.CheckLockStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ExportBackupViewModel(
    private val checkLockStatus: CheckLockStatus,
    private val exportBackupSuspended: ExportBackupSuspended,
    private val filesProvider: FilesProvider,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ExportBackupUiState())
    val uiState = _uiState.asStateFlow()

    private var backupPassword: String = ""
    private var exportType = ExportType.Download

    private enum class ExportType { Download, Share }
}