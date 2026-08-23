package com.twofasapp.feature.externalimport.ui.result

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.legacyDecodeBase64
import com.twofasapp.core.design.foundation.dialog.formatErrorDetails
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.feature.externalimport.domain.AegisImporter
import com.twofasapp.feature.externalimport.domain.AndOtpImporter
import com.twofasapp.feature.externalimport.domain.AuthenticatorProImporter
import com.twofasapp.feature.externalimport.domain.ExternalImport
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.externalimport.domain.LastPassImporter
import com.twofasapp.feature.externalimport.domain.RaivoImporter
import com.twofasapp.feature.qrscan.ReadQrFromImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class ExternalImportResultViewModel(
    private val importType: ImportType,
    private val importFileUri: String?,
    private val importFileContent: String?,
    private val servicesRepository: ServicesRepository,
    private val readQrFromImage: ReadQrFromImage,
    private val googleAuthenticatorImporter: GoogleAuthenticatorImporter,
    private val aegisImporter: AegisImporter,
    private val raivoImporter: RaivoImporter,
    private val lastPassImporter: LastPassImporter,
    private val authenticatorProImporter: AuthenticatorProImporter,
    private val andOtpImporter: AndOtpImporter,
) : ViewModel() {

    val uiState = MutableStateFlow(ExternalImportResultUiState())

    init {
        uiState.update { state ->
            state.copy(
                importType = importType,
            )
        }

        launchScoped {
            val result = when (importType) {
                ImportType.GoogleAuthenticator -> {
                    if (importFileContent != null) {
                        googleAuthenticatorImporter.read(importFileContent.legacyDecodeBase64())
                    } else if (importFileUri != null) {
                        val readQrResult = readQrFromImage.invoke(Uri.parse(importFileUri))

                        if (readQrResult.isSuccess) {
                            googleAuthenticatorImporter.read(readQrResult.getOrNull().orEmpty())
                        } else {
                            ExternalImport.FileReadError("Could not read Google Authenticator QR code")
                        }
                    } else {
                        ExternalImport.FileReadError("Could not read Google Authenticator content")
                    }
                }

                ImportType.Aegis -> aegisImporter.read(importFileUri.orEmpty().legacyDecodeBase64())
                ImportType.Raivo -> raivoImporter.read(importFileUri.orEmpty().legacyDecodeBase64())
                ImportType.LastPass -> lastPassImporter.read(importFileUri.orEmpty().legacyDecodeBase64())
                ImportType.AuthenticatorPro -> authenticatorProImporter.read(importFileUri.orEmpty().legacyDecodeBase64())
                ImportType.AndOtp -> andOtpImporter.read(importFileUri.orEmpty().legacyDecodeBase64())
            }

            uiState.update { state ->
                state.copy(
                    loading = false,
                    readResult = when (result) {
                        is ExternalImport.Success -> ReadResult.Success(
                            services = result.servicesToImport,
                            countServicesToImport = result.servicesToImport.size,
                            countTotalServices = result.totalServicesCount,
                        )

                        is ExternalImport.ParsingError -> ReadResult.Failure(reason = result.reason.formatErrorDetails())
                        is ExternalImport.UnsupportedError -> ReadResult.Failure(reason = result.reason)
                        is ExternalImport.FileReadError -> ReadResult.Failure(reason = result.reason)
                    },
                )
            }
        }
    }

    fun importServices() {
        launchScoped {
            uiState.update { it.copy(loading = true) }

            (uiState.value.readResult as? ReadResult.Success)?.let { result ->
                servicesRepository.addServices(result.services)
                uiState.update { it.copy(finishSuccess = true) }
            }
        }
    }
}