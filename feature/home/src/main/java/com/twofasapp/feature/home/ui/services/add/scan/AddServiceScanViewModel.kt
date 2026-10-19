package com.twofasapp.feature.home.ui.services.add.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.common.domain.logParams
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.common.logger.Flog
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.services.otp.OtpLinkParser
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.feature.qrscan.ReadQrFromImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

internal class AddServiceScanViewModel(
    private val servicesRepository: ServicesRepository,
    private val readQrFromImage: ReadQrFromImage,
    private val googleAuthenticatorImporter: GoogleAuthenticatorImporter,
) : ViewModel() {

    val uiState: MutableStateFlow<AddServiceScanUiState> = MutableStateFlow(AddServiceScanUiState())

    enum class ScanSource { Scan, Gallery }

    fun onScanned(text: String) {
        // The camera analyzer delivers frames continuously on a background thread, so this
        // can fire many times before `enabled` propagates back to the composable gate.
        // Atomically flip the flag here so only the first frame is processed - otherwise
        // concurrent addService() calls race, delete each other's freshly-inserted rows,
        // and the success screen ends up pointing at a serviceId that was already removed.
        val wasEnabled = uiState.getAndUpdate { it.copy(scanned = text, enabled = false) }.enabled
        if (wasEnabled.not()) return

        Timber.d("Scanned: $text")
        handleScanned(text, ScanSource.Scan)
    }

    fun onLoadFromGallery(uri: Uri) {
        launchScoped {
            readQrFromImage.invoke(uri)
                .onSuccess { text ->
                    uiState.update { it.copy(scanned = text) }
                    handleScanned(text, ScanSource.Gallery)
                }
                .onFailure { e ->
                    Flog.persist(tag = "AddService", message = "Gallery QR read failed (${e::class.simpleName})")
                    uiState.update { it.copy(showGalleryErrorDialog = true) }
                }
        }
    }

    private fun handleScanned(text: String, source: ScanSource) {
        launchScoped {
            uiState.update { it.copy(source = source) }

            // Google Authenticator export codes are handled by the external import flow
            if (googleAuthenticatorImporter.isSchemaSupported(text)) {
                val batch = googleAuthenticatorImporter.readBatch(text)
                Flog.persist(tag = "AddService", message = "$source: Google Authenticator export detected (batch=${batch?.size})")
                uiState.update {
                    it.copy(
                        googleAuthenticatorImport = GoogleAuthenticatorImport(
                            content = text,
                            isBatch = batch?.isMultiCode == true,
                            fromGallery = source == ScanSource.Gallery,
                        ),
                    )
                }
                return@launchScoped
            }

            val link = OtpLinkParser.parse(text)

            if (link == null) {
                // Only the scheme is logged - QR content may contain secrets
                Flog.persist(tag = "AddService", message = "$source: invalid QR (scheme=${text.qrScheme()})")
                uiState.update { it.copy(showInvalidQrDialog = true) }
                return@launchScoped
            }

            if (servicesRepository.isServiceValid(link).not()) {
                Flog.persist(tag = "AddService", message = "$source: unsupported params ${link.logParams()}")
                uiState.update { it.copy(showInvalidQrDialog = true) }
                return@launchScoped
            }

            if (servicesRepository.isServiceExists(link.secret)) {
                Flog.persist(tag = "AddService", message = "$source: service already exists")
                uiState.update { it.copy(showServiceExistsDialog = true) }
                return@launchScoped
            }

            saveScannedService(text, source)
        }
    }

    fun saveScannedService(text: String, source: ScanSource) {
        saveScannedService(OtpLinkParser.parse(text)!!, source)
    }

    private fun saveScannedService(link: OtpAuthLink, source: ScanSource) {
        launchScoped {
            runSafely { servicesRepository.addService(link) }
                .onSuccess { serviceId ->
                    uiState.update { state ->
                        state.copy(
                            addedService = RecentlyAddedService(
                                serviceId = serviceId,
                                source = when (source) {
                                    ScanSource.Scan -> RecentlyAddedService.Source.QrScan
                                    ScanSource.Gallery -> RecentlyAddedService.Source.QrGallery
                                },
                            ),
                        )
                    }
                }
                .onFailure { e ->
                    Flog.persist(tag = "AddService", message = "$source: save failed (${e::class.simpleName})")
                    uiState.update { it.copy(showErrorDialog = true) }
                }
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
            delay(500.milliseconds)
            uiState.update { it.copy(enabled = true) }
        }
    }
}

private fun String.qrScheme(): String {
    return substringBefore(':', missingDelimiterValue = "").take(32).ifEmpty { "none" }
}