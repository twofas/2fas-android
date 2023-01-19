package com.twofasapp.browserextension.ui.pairing.scan

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.qrscanner.domain.ScanQr
import com.twofasapp.resources.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class PairingScanViewModel(
    private val scanQr: ScanQr,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(PairingScanUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                scanQr.observeResultFlow().collect {
                    try {
                        val uri = Uri.parse(it.text)

                        if (uri.scheme.equals("twofas_c").not()) {
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
                                extensionId = it.text.removePrefix("twofas_c://")
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
}
