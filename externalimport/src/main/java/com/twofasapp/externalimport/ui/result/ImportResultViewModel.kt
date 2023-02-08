package com.twofasapp.externalimport.ui.result

import androidx.lifecycle.viewModelScope
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.core.encoding.decodeBase64
import com.twofasapp.externalimport.domain.AegisImporter
import com.twofasapp.externalimport.domain.ExternalImport
import com.twofasapp.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.externalimport.domain.RaivoImporter
import com.twofasapp.navigation.ExternalImportDirections.ImportResult.Type
import com.twofasapp.resources.R
import com.twofasapp.services.data.converter.toService
import com.twofasapp.services.domain.AddServiceCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ImportResultViewModel(
    private val dispatchers: Dispatchers,
    private val googleAuthenticatorImporter: GoogleAuthenticatorImporter,
    private val aegisImporter: AegisImporter,
    private val raivoImporter: RaivoImporter,
    private val addServiceCase: AddServiceCase,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ImportResultUiState())
    val uiState = _uiState.asStateFlow()

    fun import(type: Type, content: String) {
        viewModelScope.launch(dispatchers.io()) {
            val result = when (type) {
                Type.GoogleAuthenticator -> googleAuthenticatorImporter.read(content.decodeBase64())
                Type.Aegis -> aegisImporter.read(content.decodeBase64())
                Type.Raivo -> raivoImporter.read(content.decodeBase64())
            }

            when (result) {
                is ExternalImport.Success -> {
                    _uiState.update {
                        ImportResultUiState(
                            importResult = result,
                            title = getTitle(type),
                            description = getSuccessDescription(type),
                            servicesToImport = result.servicesToImport.size,
                            totalServicesCount = result.totalServicesCount,
                            footer = R.string.tokens__google_auth_import_subtitle_end,
                            button = R.string.commons__proceed
                        )
                    }
                }

                is ExternalImport.ParsingError,
                ExternalImport.UnsupportedError -> {
                    _uiState.update {
                        ImportResultUiState(
                            importResult = result,
                            title = getTitle(type),
                            description = R.string.externalimport__read_error,
                            servicesToImport = 0,
                            totalServicesCount = 0,
                            footer = null,
                            button = R.string.commons__try_again
                        )
                    }
                }
            }
        }
    }

    fun saveServices() {
        if (uiState.value.importResult is ExternalImport.Success) {

            viewModelScope.launch(dispatchers.io()) {
                (uiState.value.importResult as ExternalImport.Success).servicesToImport.asFlow()
                    .onEach { addServiceCase(it.toService()) }
                    .onCompletion {
                        syncBackupDispatcher.dispatch(SyncBackupTrigger.SERVICES_CHANGED)
                        _uiState.update { it.copy(finishSuccess = true) }
                    }
                    .collect()
            }
        }
    }

    private fun getTitle(type: Type) = when (type) {
        Type.GoogleAuthenticator -> R.string.externalimport__ga_title
        Type.Aegis -> R.string.externalimport__aegis_title
        Type.Raivo -> R.string.externalimport__raivo_title
    }

    private fun getSuccessDescription(type: Type) = when (type) {
        Type.GoogleAuthenticator -> R.string.externalimport__ga_success_msg
        Type.Aegis -> R.string.externalimport__aegis_success_msg
        Type.Raivo -> R.string.externalimport__raivo_success_msg
    }
}
