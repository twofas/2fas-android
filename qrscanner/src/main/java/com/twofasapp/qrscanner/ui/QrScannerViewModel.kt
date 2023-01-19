package com.twofasapp.qrscanner.ui

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.qrscanner.domain.ReadQrFromImageCase
import com.twofasapp.qrscanner.domain.ScanQr
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class QrScannerViewModel(
    private val dispatchers: Dispatchers,
    private val scanQr: ScanQr,
    private val readQrFromImageCase: ReadQrFromImageCase,
) : BaseViewModel() {

    private var isAcceptingImage = true

    val showPhotoReadError = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            scanQr.observeResetFlow().collect {
                resetWithDelay()
            }
        }
    }

    fun onScanned(text: String) {
        if (isAcceptingImage) {
            scanQr.publishResult(ScanQr.Result(text))
            isAcceptingImage = false
            Timber.d(text)
        }
    }

    fun onPhotoPicked(uri: Uri) {
        viewModelScope.launch(dispatchers.io()) {
            val result = readQrFromImageCase(uri)

            when (result) {
                is ReadQrFromImageCase.Result.Success -> scanQr.publishResult(ScanQr.Result(result.content, isFromGallery = true))
                is ReadQrFromImageCase.Result.Error -> showPhotoReadError.update { true }
            }
        }
    }

    private suspend fun resetWithDelay() {
        delay(2000)
        isAcceptingImage = true
    }
}