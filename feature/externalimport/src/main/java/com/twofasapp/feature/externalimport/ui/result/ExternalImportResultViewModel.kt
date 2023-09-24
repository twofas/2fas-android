package com.twofasapp.feature.externalimport.ui.result

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.android.navigation.getOrThrowNullable
import com.twofasapp.common.ktx.decodeBase64
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.feature.externalimport.domain.AegisImporter
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
    savedStateHandle: SavedStateHandle,
    private val servicesRepository: ServicesRepository,
    private val readQrFromImage: ReadQrFromImage,
    private val googleAuthenticatorImporter: GoogleAuthenticatorImporter,
    private val aegisImporter: AegisImporter,
    private val raivoImporter: RaivoImporter,
    private val lastPassImporter: LastPassImporter,
    private val authenticatorProImporter: AuthenticatorProImporter,
) : ViewModel() {

    private val importType: ImportType = enumValueOf(savedStateHandle.getOrThrow(NavArg.ImportType.name))
    private val importFileUri = savedStateHandle.getOrThrowNullable<String>(NavArg.ImportFileUri.name)
    private val importFileContent = savedStateHandle.getOrThrowNullable<String>(NavArg.ImportFileContent.name)

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
                        googleAuthenticatorImporter.read(importFileContent.decodeBase64())
                    } else if (importFileUri != null) {
                        val readQrResult = readQrFromImage.invoke(Uri.parse(importFileUri))

                        if (readQrResult.isSuccess) {
                            googleAuthenticatorImporter.read(readQrResult.getOrNull().orEmpty())
                        } else {
                            ExternalImport.UnsupportedError
                        }
                    } else {
                        ExternalImport.UnsupportedError
                    }
                }

                ImportType.Aegis -> aegisImporter.read(importFileUri.orEmpty().decodeBase64())
                ImportType.Raivo -> raivoImporter.read(importFileUri.orEmpty().decodeBase64())
                ImportType.LastPass -> lastPassImporter.read(importFileUri.orEmpty().decodeBase64())
                ImportType.AuthenticatorPro -> authenticatorProImporter.read(importFileUri.orEmpty().decodeBase64())
            }

            uiState.update { state ->
                state.copy(
                    loading = false,
                    readResult = when (result) {
                        is ExternalImport.Success -> ReadResult.Success(
                            services = result.servicesToImport,
                            countServicesToImport = result.servicesToImport.size,
                            countTotalServices = result.totalServicesCount
                        )

                        is ExternalImport.ParsingError -> ReadResult.Failure
                        ExternalImport.UnsupportedError -> ReadResult.Failure
                    }
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