package com.twofasapp.feature.home.ui.services.add.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.services.otp.OtpLinkParser
import com.twofasapp.feature.qrscan.ReadQrFromImage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

internal class AddServiceScanViewModel(
    private val servicesRepository: ServicesRepository,
    private val readQrFromImage: ReadQrFromImage,
) : ViewModel() {

    val uiState: MutableStateFlow<AddServiceScanUiState> = MutableStateFlow(AddServiceScanUiState())
    val uiEvents: MutableSharedFlow<AddServiceScanUiEvent> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    enum class Source { Scan, Gallery }

    fun onScanned(text: String) {
        Timber.d("Scanned: $text")
        uiState.update {
            it.copy(
                scanned = text,
                enabled = false,
            )
        }

        tryInsertService(text, Source.Scan)
    }

    fun onLoadFromGallery(uri: Uri) {
        launchScoped {
            readQrFromImage.invoke(uri)
                .onSuccess { text ->
                    uiState.update { it.copy(scanned = text) }
                    tryInsertService(text, Source.Gallery)
                }
                .onFailure { uiState.update { it.copy(showGalleryErrorDialog = true) } }
        }
    }

    private fun tryInsertService(text: String, source: Source) {
        launchScoped {
            uiState.update { it.copy(source = source) }

            val link = OtpLinkParser.parse(text)

            if (link == null) {
                uiState.update { it.copy(showInvalidQrDialog = true) }
                return@launchScoped
            }

            if (servicesRepository.isServiceValid(link).not()) {
                uiState.update { it.copy(showInvalidQrDialog = true) }
                return@launchScoped
            }

            if (servicesRepository.isServiceExists(link.secret)) {
                uiState.update { it.copy(showServiceExistsDialog = true) }
                return@launchScoped
            }

            saveService(text, source)
        }
    }

    fun saveService(text: String, source: Source) {
        saveService(OtpLinkParser.parse(text)!!, source)
    }

    private fun saveService(link: OtpAuthLink, source: Source) {
        launchScoped {
            runSafely { servicesRepository.addService(link) }
                .onSuccess {
                    uiEvents.emit(
                        AddServiceScanUiEvent.AddedSuccessfully(
                            RecentlyAddedService(
                                serviceId = it,
                                source = when (source) {
                                    Source.Scan -> RecentlyAddedService.Source.QrScan
                                    Source.Gallery -> RecentlyAddedService.Source.QrGallery
                                }
                            )
                        )
                    )
                }
                .onFailure { uiState.update { it.copy(showErrorDialog = true) } }
        }
    }

    fun resetScanner() {
        uiState.update {
            it.copy(
                showInvalidQrDialog = false,
                showServiceExistsDialog = false,
                showErrorDialog = false,
                showGalleryErrorDialog = false,
            )
        }

        launchScoped {
            delay(500)
            uiState.update { it.copy(enabled = true) }
        }
    }
}
