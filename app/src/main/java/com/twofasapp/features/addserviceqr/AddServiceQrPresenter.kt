package com.twofasapp.features.addserviceqr

import android.net.Uri
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.extensions.removeWhiteCharacters
import com.twofasapp.feature.externalimport.domain.ExternalImport
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.OtpAuthLink
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.usecase.LastScannedQrPreference
import com.twofasapp.qrscanner.domain.ReadQrFromImageRx
import com.twofasapp.qrscanner.domain.ScanQr
import com.twofasapp.services.domain.ConvertOtpLinkToService
import com.twofasapp.usecases.services.AddService
import com.twofasapp.usecases.services.CheckServiceExists
import com.twofasapp.usecases.services.GetService
import com.twofasapp.usecases.totp.ParseOtpAuthLink
import io.reactivex.rxkotlin.toFlowable

class AddServiceQrPresenter(
    private val view: AddServiceQrContract.View,
    private val navigator: ScopedNavigator,
    private val parseOtpAuthLink: ParseOtpAuthLink,
    private val addService: AddService,
    private val checkServiceExists: CheckServiceExists,
    private val analyticsService: com.twofasapp.core.analytics.AnalyticsService,
    private val convertOtpLinkToService: ConvertOtpLinkToService,
    private val scanQr: ScanQr,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
    private val getService: GetService,
    private val readQrFromImageRx: ReadQrFromImageRx,
    private val googleAuthenticatorImporter: GoogleAuthenticatorImporter,
    private val appBuild: AppBuild,
    private val lastScannedQrPreference: LastScannedQrPreference,
    private val servicesRepository: ServicesRepository,
) : AddServiceQrContract.Presenter() {

    private lateinit var otpAuthLink: OtpAuthLink
    private var isAcceptingScans = true

    override fun onViewAttached() {
        view.toolbarBackClicks()
            .safelySubscribe { navigator.navigateBack() }

        scanQr.observeResult()
            .safelySubscribe(
                onNext = { onQrScanned(false, it.text) },
                onError = { scanQr.publishReset() }
            )
    }

    override fun onQrLoadedFromGallery(data: Uri) {
        isAcceptingScans = false
        readQrFromImageRx.execute(data)
            .safelySubscribe {
                when (it) {
                    is ReadQrFromImageRx.Result.Success -> onQrScanned(true, it.content)
                    is ReadQrFromImageRx.Result.Error -> onSaveFailed(true, null)
                }
            }
    }

    private fun onQrScanned(isFromGallery: Boolean, content: String) {
        if (isAcceptingScans.not() && isFromGallery.not()) return

        when {
            isGoogleAuthenticatorLink(content) -> importFromGoogleAuthenticator(isFromGallery, content)
            isMarketLink(content) -> view.showIncorrectQrStoreLink { resetScanner() }
            else -> {
                if (appBuild.isDebuggable) {
                    lastScannedQrPreference.put(content)
                }

                parseOtpAuthLink.execute(ParseOtpAuthLink.Params(content))
                    .doOnSuccess { otpAuthLink = it }
                    .flatMap { checkServiceExists.execute(it.secret) }
                    .safelySubscribe(onSuccess = { onCheckServiceExists(it, isFromGallery) }, onError = { onSaveFailed(isFromGallery, it) })
            }
        }
    }

    private fun onCheckServiceExists(exists: Boolean, isFromGallery: Boolean) {
        if (exists) {
            view.showServiceExistsDialog(
                confirmAction = { saveService(isFromGallery = isFromGallery) },
                cancelAction = {
                    isAcceptingScans = true
                    scanQr.publishReset()
                }
            )
        } else {
            saveService(isFromGallery = isFromGallery)
        }
    }

    private fun importFromGoogleAuthenticator(isFromGallery: Boolean, content: String) {
        when (val result = googleAuthenticatorImporter.read(content)) {
            is ExternalImport.Success -> {
                view.showImportGoogleAuthenticator(
                    servicesCount = result.servicesToImport.size,
                    totalServicesCount = result.totalServicesCount,
                    okAction = {
                        if (result.servicesToImport.isNotEmpty()) {
                            result.servicesToImport.toFlowable()
                                .concatMapCompletable { addService.execute(AddService.Params(it)) }
                                .doOnComplete { syncBackupDispatcher.tryDispatch(SyncBackupTrigger.SERVICES_CHANGED) }
                                .safelySubscribe {
                                    analyticsService.captureEvent(com.twofasapp.core.analytics.AnalyticsEvent.IMPORT_GOOGLE_AUTHENTICATOR)
                                    view.showSuccessImportToast()
                                    navigator.finish()
                                }
                        } else {
                            if (isFromGallery) {
                                view.startGalleryPicker()
                            } else {
                                view.dismissInfoDialog()
                                isAcceptingScans = true
                                resetScanner()
                            }
                        }
                    },
                    dismissAction = { resetScanner() }
                )
                return
            }

            is ExternalImport.ParsingError -> onSaveFailed(isFromGallery, result.reason)
            ExternalImport.UnsupportedError -> onSaveFailed(isFromGallery, null)
        }
    }

    private fun saveService(newName: String? = null, isFromGallery: Boolean) {
        var service = convertOtpLinkToService.execute(otpAuthLink)

        if (service.name.isBlank() && newName.isNullOrBlank()) {
            view.showInputNameDialog(onNameConfirmedAction = { saveService(it, isFromGallery) }, onNameDismissAction = {
                isAcceptingScans = true
                resetScanner()
            })
            return
        }

        if (service.name.isBlank()) {
            service = service.copy(name = newName!!.trim())
        }

        addService.execute(AddService.Params(service))
            .doOnComplete { syncBackupDispatcher.tryDispatch(SyncBackupTrigger.SERVICES_CHANGED) }
            .andThen(getService.execute(service.secret))
            .safelySubscribe(onSuccess = { onSaveCompleted(it, isFromGallery) }, onError = { onSaveFailed(false, it) })
    }

    private fun onSaveCompleted(serviceDto: ServiceDto, isFromGallery: Boolean) {
        servicesRepository.pushRecentlyAddedService(
            id = serviceDto.id,
            source = if (isFromGallery) {
                RecentlyAddedService.Source.QrGallery
            } else {
                RecentlyAddedService.Source.QrScan
            }
        )

        navigator.finishResultOk(
            mapOf(
                AddServiceQrActivity.RESULT_SERVICE to serviceDto.copy(secret = serviceDto.secret.removeWhiteCharacters()),
                AddServiceQrActivity.RESULT_IS_FROM_GALLERY to isFromGallery,
            )
        )
    }

    private fun onSaveFailed(isFromGallery: Boolean, throwable: Throwable?) {
        analyticsService.captureException(throwable)

        if (isFromGallery) {
            isAcceptingScans = false
            view.showIncorrectQrFromGallery(
                okAction = { view.startGalleryPicker() },
                dismissAction = {
                    isAcceptingScans = true
                    resetScanner()
                }
            )
        } else {
            view.showIncorrectQr { resetScanner() }
        }
    }

    private fun resetScanner() {
        scanQr.publishReset()
    }

    private fun isGoogleAuthenticatorLink(content: String) =
        googleAuthenticatorImporter.isSchemaSupported(content)

    private fun isMarketLink(content: String) =
        content.startsWith("market://") || content.contains("play.google.com/store/apps")
}
