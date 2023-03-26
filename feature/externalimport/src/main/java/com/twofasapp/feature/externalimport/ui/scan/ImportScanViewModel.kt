package com.twofasapp.feature.externalimport.ui.scan

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.qrscanner.domain.ScanQr
import com.twofasapp.resources.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ImportScanViewModel(
    private val dispatchers: Dispatchers,
    private val scanQr: ScanQr,
    private val googleAuthenticatorImporter: GoogleAuthenticatorImporter,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ImportScanUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.io()) {

            scanQr.observeResultFlow().collect {
                try {
                    val uri = Uri.parse(it.text)

                    val isSupported = googleAuthenticatorImporter.isSchemaSupported(uri.scheme.orEmpty())

                    if (isSupported.not()) {
                        _uiState.update { state ->
                            state.copy(
                                isSuccess = false,
                                showErrorDialog = true,
                                errorDialogTitle = R.string.browser__scan_error_dialog_title,
                                errorDialogMessage = R.string.browser__scan_error_dialog_msg_invalid_code,
                                errorDialogAction = { scanQr.publishReset() }
                            )
                        }
                        return@collect
                    }

                    _uiState.update { state ->
                        state.copy(
                            isSuccess = true,
                            showErrorDialog = false,
                            content = it.text
                        )
                    }

                } catch (e: Exception) {
                    _uiState.update { state ->
                        state.copy(
                            isSuccess = false,
                            showErrorDialog = true,
                            errorDialogTitle = R.string.browser__scan_error_dialog_title,
                            errorDialogMessage = R.string.browser__scan_error_dialog_msg_unknown,
                            errorDialogAction = { scanQr.publishReset() }
                        )
                    }
                }
            }
        }

    }
}
