package com.twofasapp.features.addserviceqr

import android.net.Uri
import com.twofasapp.design.dialogs.CancelAction
import com.twofasapp.design.dialogs.ConfirmAction
import io.reactivex.Flowable

interface AddServiceQrContract {

    interface View {
        fun toolbarBackClicks(): Flowable<Unit>

        fun startGalleryPicker()
        fun showServiceExistsDialog(confirmAction: ConfirmAction, cancelAction: CancelAction)
        fun showInputNameDialog(onNameConfirmedAction: OnNameConfirmedAction, onNameDismissAction: OnNameDismissAction)
        fun showIncorrectQr(okAction: OkAction)
        fun showIncorrectQrStoreLink(okAction: OkAction)
        fun showIncorrectQrFromGallery(okAction: OkAction, dismissAction: DismissAction)
        fun showImportGoogleAuthenticator(servicesCount: Int, totalServicesCount: Int, okAction: OkAction, dismissAction: DismissAction)
        fun showSuccessImportToast()
        fun dismissInfoDialog()
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun onQrLoadedFromGallery(data: Uri)
    }
}
