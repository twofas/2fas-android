package com.twofasapp.features.main

import com.twofasapp.design.dialogs.CancelAction
import com.twofasapp.design.dialogs.ConfirmAction
import com.twofasapp.prefs.model.ServiceDto

interface MainContract {

    interface View {
        fun showLoginRequestDialog(title: String, content: String, authenticationId: Int)
        fun showRateApp()
        fun showUpgradeAppNoticeDialog(action: () -> Unit)
        fun showServiceExistsDialog(confirmAction: ConfirmAction, cancelAction: CancelAction)
        fun showRemoveQrReminder(serviceDto: ServiceDto)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun startObservingPushes()
        abstract fun stopObservingPushes()
        abstract fun onReviewSuccess()
        abstract fun onReviewFailed(exception: Exception?)
    }
}
