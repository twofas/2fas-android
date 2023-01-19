package com.twofasapp.features.main

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.design.dialogs.CancelAction
import com.twofasapp.design.dialogs.ConfirmAction

interface MainContract {

    interface View {
        fun showLoginRequestDialog(title: String, content: String, authenticationId: Int)

        fun openDrawer()
        fun closeDrawer(delay: Long = 0)
        fun getStringFromRes(resId: Int): String
        fun showRationaleDialog(title: Int, content: Int)
        fun showSnackbarLong(msgRes: Int)
        fun showSnackbarShort(message: Int)
        fun showServiceBottomSheet(serviceDto: ServiceDto)
        fun setDrawerItems(entries: List<DrawerEntry>)
        fun setToolbarState(state: ToolbarState)
        fun setEditButtonVisible(isVisible: Boolean)
        fun setSearchButtonVisible(isVisible: Boolean)
        fun setAddGroupButtonVisible(isVisible: Boolean)
        fun setSortButtonVisible(isVisible: Boolean)
        fun setSearchVisible(isVisible: Boolean)
        fun setFabVisible(isVisible: Boolean)
        fun setSyncFabVisible(isVisible: Boolean)
        fun showCreateGroupDialog(onSaveAction: (String) -> Unit)
        fun showRateApp()
        fun showUpgradeAppNoticeDialog(action: () -> Unit)
        fun showSortDialog(initialSelection: Int, onSelectAction: (Int) -> Unit)
        fun showServiceExistsDialog(confirmAction: ConfirmAction, cancelAction: CancelAction)
        fun checkAppVersionUpdate()
        fun showRemoveQrReminder(serviceDto: ServiceDto)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun onAddServiceManuallyClick()
        abstract fun onAddServiceQrClick()
        abstract fun onAddGroupClick()
        abstract fun onSortClick()
        abstract fun startObservingPushes()
        abstract fun stopObservingPushes()
        abstract fun showSnackbarShort(message: Int)
        abstract fun showServiceBottomSheet(serviceDto: ServiceDto, isFromGallery: Boolean)
        abstract fun onReviewSuccess()
        abstract fun onReviewFailed(exception: Exception?)
        abstract fun canDisplayAppUpdate(): Boolean
        abstract fun markAppUpdateDisplayed()
        abstract fun updateUnreadNotifications(hasUnreadNotifications: Boolean)
    }
}
