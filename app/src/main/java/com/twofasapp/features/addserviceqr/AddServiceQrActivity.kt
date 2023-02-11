package com.twofasapp.features.addserviceqr

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.core.log.FileLogger
import com.twofasapp.databinding.ActivityAddServiceQrBinding
import com.twofasapp.design.dialogs.CancelAction
import com.twofasapp.design.dialogs.ConfirmAction
import com.twofasapp.design.dialogs.ConfirmDialog
import com.twofasapp.extensions.navigationClicksThrottled
import com.twofasapp.extensions.toastLong
import com.twofasapp.resources.R
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans

class AddServiceQrActivity : BaseActivityPresenter<ActivityAddServiceQrBinding>(), AddServiceQrContract.View {

    companion object {
        const val REQUEST_CODE = 43251
        const val GALLERY_REQUEST_CODE = 12467
        const val RESULT_SERVICE = "service"
        const val RESULT_IS_FROM_GALLERY = "isFromGallery"
    }

    private val presenter: AddServiceQrContract.Presenter by injectThis()
    private val serviceExistsDialog: ConfirmDialog by lazy { ConfirmDialog(this, R.string.commons__warning, R.string.tokens__service_already_exists) }
    private val inputNameDialog: InputNameDialog by lazy { InputNameDialog(this) }
    private val scanInfoDialog: ScanInfoDialog by lazy { ScanInfoDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FileLogger.logScreen("AddServiceQr")
        setContentView(ActivityAddServiceQrBinding::inflate)
        setPresenter(presenter)
        viewBinding.appBar.outlineProvider = null
        viewBinding.openGallery.setOnClickListener { startGalleryPicker() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            presenter.onQrLoadedFromGallery(data.data!!)
        }
    }

    override fun startGalleryPicker() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE, null)
        } catch (e: Exception) {
            toastLong(getString(R.string.intent_error_no_gallery_app))
        }
    }

    override fun toolbarBackClicks() = viewBinding.toolbar.navigationClicksThrottled()

    override fun showServiceExistsDialog(confirmAction: ConfirmAction, cancelAction: CancelAction) =
        serviceExistsDialog.show(confirmAction = confirmAction, cancelAction = cancelAction)

    override fun showInputNameDialog(onNameConfirmedAction: OnNameConfirmedAction, onNameDismissAction: OnNameDismissAction) =
        inputNameDialog.show(onNameConfirmedAction, onNameDismissAction)

    override fun showSuccessImportToast() {
        toastLong(getString(R.string.import_ga_success))
    }

    override fun showIncorrectQr(okAction: OkAction) =
        scanInfoDialog.show(
            title = getString(R.string.tokens__qr_does_not_work),
            desc = getString(R.string.tokens__qr_point_and_scan_again),
            okText = getString(R.string.tokens__try_again),
            imageRes = R.drawable.incorrect_qr,
            action = okAction
        )

    override fun showIncorrectQrStoreLink(okAction: OkAction) =
        scanInfoDialog.show(
            title = getString(R.string.tokens__qr_code_leads_to_app_store),
            desc = getString(R.string.tokens__qr_point_and_scan_again),
            okText = getString(R.string.tokens__try_again),
            imageRes = R.drawable.incorrect_qr_store,
            action = okAction
        )

    override fun showIncorrectQrFromGallery(okAction: OkAction, dismissAction: DismissAction) =
        scanInfoDialog.show(
            title = getString(R.string.tokens__qr_read_image_failed),
            desc = getString(R.string.tokens__qr_read_image_try_again),
            okText = getString(R.string.tokens__try_again),
            imageRes = R.drawable.incorrect_qr,
            action = okAction,
            actionDismiss = dismissAction
        )

    override fun showImportGoogleAuthenticator(servicesCount: Int, totalServicesCount: Int, okAction: OkAction, dismissAction: DismissAction) {
        val counter = if (servicesCount == totalServicesCount) {
            servicesCount.toString()
        } else {
            getString(R.string.tokens__google_auth_out_of_title).format(servicesCount, totalServicesCount)
        }

        val desc = if (servicesCount == 0) {
            Spanner().append("\n${getString(R.string.externalimport__no_tokens_msg)}")
        } else {
            Spanner()
                .append("\n${getString(R.string.tokens__google_auth_import_subtitle)}\n\n")
                .append(counter, Spans.bold(), Spans.sizeDP(24))
                .append("\n\n")
                .append(getString(R.string.tokens__google_auth_import_subtitle_end))
        }

        val okText = if (servicesCount == 0) {
            getString(R.string.commons__try_again)
        } else {
            getString(R.string.commons__proceed)
        }

        scanInfoDialog.show(
            title = getString(R.string.tokens__google_auth_import),
            desc = "",
            descSpan = desc,
            okText = okText,
            imageRes = R.drawable.ic_import_ga,
            action = okAction,
            actionDismiss = dismissAction
        )
    }

    override fun dismissInfoDialog() {
        scanInfoDialog.dismiss()
    }
}
