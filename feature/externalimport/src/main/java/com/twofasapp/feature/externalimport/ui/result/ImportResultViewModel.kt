package com.twofasapp.feature.externalimport.ui.result

import androidx.lifecycle.viewModelScope
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.core.encoding.decodeBase64
import com.twofasapp.feature.externalimport.domain.AegisImporter
import com.twofasapp.feature.externalimport.domain.AuthenticatorProImporter
import com.twofasapp.feature.externalimport.domain.ExternalImport
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.feature.externalimport.domain.LastPassImporter
import com.twofasapp.feature.externalimport.domain.RaivoImporter
import com.twofasapp.feature.externalimport.navigation.ImportType
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
    private val addServiceCase: AddServiceCase,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
    private val googleAuthenticatorImporter: GoogleAuthenticatorImporter,
    private val aegisImporter: AegisImporter,
    private val raivoImporter: RaivoImporter,
    private val lastPassImporter: LastPassImporter,
    private val authenticatorProImporter: AuthenticatorProImporter,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ImportResultUiState())
    val uiState = _uiState.asStateFlow()

    fun import(type: ImportType, content: String) {
        viewModelScope.launch(dispatchers.io()) {
            val result = when (type) {
                ImportType.GoogleAuthenticator -> googleAuthenticatorImporter.read(content.decodeBase64())
                ImportType.Aegis -> aegisImporter.read(content.decodeBase64())
                ImportType.Raivo -> raivoImporter.read(content.decodeBase64())
                ImportType.LastPass -> lastPassImporter.read(content.decodeBase64())
                ImportType.AuthenticatorPro -> authenticatorProImporter.read(content.decodeBase64())
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
                        syncBackupDispatcher.tryDispatch(SyncBackupTrigger.ServicesChanged)
                        _uiState.update { it.copy(finishSuccess = true) }
                    }
                    .collect()
            }
        }
    }

    private fun getTitle(type: ImportType) = when (type) {
        ImportType.GoogleAuthenticator -> R.string.externalimport__ga_title
        ImportType.Aegis -> R.string.externalimport__aegis_title
        ImportType.Raivo -> R.string.externalimport__raivo_title
        ImportType.LastPass -> R.string.externalimport__lastpass_title
        ImportType.AuthenticatorPro -> R.string.externalimport__authenticatorpro_title
    }

    private fun getSuccessDescription(type: ImportType) = when (type) {
        ImportType.GoogleAuthenticator -> R.string.externalimport__ga_success_msg
        ImportType.Aegis -> R.string.externalimport__aegis_success_msg
        ImportType.Raivo -> R.string.externalimport__raivo_success_msg
        ImportType.LastPass -> R.string.externalimport__lastpass_success_msg
        ImportType.AuthenticatorPro -> R.string.externalimport__authenticatorpro_success_msg
    }
}
