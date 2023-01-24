package com.twofasapp.feature.externalimport.ui.result

import androidx.lifecycle.viewModelScope
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.core.encoding.decodeBase64
import com.twofasapp.feature.externalimport.domain.AegisImporter
import com.twofasapp.feature.externalimport.domain.ExternalImport
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.feature.externalimport.domain.RaivoImporter
import com.twofasapp.feature.externalimport.navigation.ImportType
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

    fun import(type: ImportType, content: String) {
        viewModelScope.launch(dispatchers.io()) {
            val result = when (type) {
                ImportType.GoogleAuthenticator -> googleAuthenticatorImporter.read(content.decodeBase64())
                ImportType.Aegis -> aegisImporter.read(content.decodeBase64())
                ImportType.Raivo -> raivoImporter.read(content.decodeBase64())
            }

            when (result) {
                is ExternalImport.Success -> {
                    _uiState.update {
                        ImportResultUiState(
                            importResult = result,
                            title = getTitle(type),
                            description = getSuccessDescription(type),
                            counter = if (result.servicesToImport.size == result.totalServicesCount) {
                                result.servicesToImport.size.toString()
                            } else {
                                "${result.servicesToImport.size} out of ${result.totalServicesCount}"
                            },
                            footer = if (result.servicesToImport.size == 1) "token will be imported." else "tokens will be imported.",
                            button = "Proceed"
                        )
                    }
                }

                is ExternalImport.ParsingError,
                ExternalImport.UnsupportedError -> {
                    _uiState.update {
                        ImportResultUiState(
                            importResult = result,
                            title = getTitle(type),
                            description = "Could not read any tokens. Try to select a different file.",
                            counter = "",
                            footer = "",
                            button = "Try again"
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

    private fun getTitle(type: ImportType) = when (type) {
        ImportType.GoogleAuthenticator -> "Importing 2FA tokens from Google Authenticator app"
        ImportType.Aegis -> "Importing 2FA tokens from Aegis app"
        ImportType.Raivo -> "Importing 2FA tokens from Raivo app"
    }

    private fun getSuccessDescription(type: ImportType) = when (type) {
        ImportType.GoogleAuthenticator -> "This QR code allows importing tokens from Google Authenticator."
        ImportType.Aegis -> "This JSON file allows importing tokens from Aegis."
        ImportType.Raivo -> "This JSON file allows importing tokens from Raivo."
    }
}
