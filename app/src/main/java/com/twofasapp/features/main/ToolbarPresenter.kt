package com.twofasapp.features.main

import com.twofasapp.resources.R
import com.twofasapp.base.BasePresenter
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.usecases.backup.ObserveSyncStatus
import com.twofasapp.usecases.backup.model.SyncStatus
import com.twofasapp.usecases.services.EditStateObserver
import com.twofasapp.usecases.services.SearchStateObserver
import com.twofasapp.usecases.services.ServicesObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.rxkotlin.Flowables

class ToolbarPresenter(
    private val view: MainContract.View,
    private val editStateObserver: EditStateObserver,
    private val searchStateObserver: SearchStateObserver,
    private val servicesObserver: ServicesObserver,
    private val observeSyncStatus: ObserveSyncStatus,
    private val storeGroups: StoreGroups,
) : BasePresenter() {

    private data class Result(
        val isEditEnabled: Boolean,
        val isSearchEnabled: Boolean,
        val isServicesEmpty: Boolean,
        val isSyncReturnedError: Boolean,
        val hasUnreadNotifications: Boolean
    )

    private val hasUnreadNotificationsProcessor = PublishProcessor.create<Boolean>()

    override fun onViewAttached() {

        Flowables.combineLatest(
            editStateObserver.observe().startWith(false),
            searchStateObserver.observeEnabled().startWith(false),
            servicesObserver.execute().map { it.isEmpty() },
            observeSyncStatus.observe().map { it is SyncStatus.Error && it.shouldShowError() }.startWith(false),
            hasUnreadNotificationsProcessor.startWith(false)
        ) { isEditEnabled, isSearchEnabled, isServicesEmpty, isSyncReturnedError, hasUnreadNotifications ->
            Result(
                isEditEnabled = isEditEnabled,
                isSearchEnabled = isSearchEnabled,
                isServicesEmpty = isServicesEmpty,
                isSyncReturnedError = isSyncReturnedError,
                hasUnreadNotifications = hasUnreadNotifications,
            )
        }
            .observeOn(AndroidSchedulers.mainThread())
            .safelySubscribe { result ->

                if (result.isServicesEmpty && storeGroups.all().list.isEmpty()) {
                    view.setEditButtonVisible(false)
                    view.setSearchButtonVisible(false)
                    view.setAddGroupButtonVisible(false)
                    view.setSortButtonVisible(false)
                } else {
                    view.setSearchButtonVisible(true)
                    view.setEditButtonVisible(result.isEditEnabled.not())
                    view.setAddGroupButtonVisible(result.isEditEnabled)
                    view.setSortButtonVisible(result.isEditEnabled)
                }

                when {
                    result.isSearchEnabled -> {
                        view.setEditButtonVisible(false)
                        view.setFabVisible(false)
                        view.setToolbarState(
                            ToolbarState(
                                titleRes = null,
                                isTitleCentered = false,
                                iconRes = R.drawable.ic_back_arrow,
                                iconContentDescription = "Close Search",
                                iconAction = { searchStateObserver.offer(false) }
                            )
                        )
                    }

                    result.isEditEnabled -> {
                        view.setSearchVisible(result.isSearchEnabled)
                        view.setFabVisible(false)
                        view.setToolbarState(
                            ToolbarState(
                                titleRes = R.string.commons__edit,
                                isTitleCentered = false,
                                iconRes = R.drawable.ic_back_arrow,
                                iconContentDescription = "Close Edit",
                                iconAction = { editStateObserver.offer(false) }
                            )
                        )
                    }

                    else -> {
                        view.setSearchVisible(false)
                        view.setFabVisible(true)
                        view.setToolbarState(
                            ToolbarState(
                                titleRes = R.string.commons__2fas_toolbar,
                                isTitleCentered = true,
                                iconRes = if (result.isSyncReturnedError || result.hasUnreadNotifications) R.drawable.ic_hamburger_badge else R.drawable.ic_hamburger,
                                iconContentDescription = "Menu",
                                iconAction = { view.openDrawer() }
                            )
                        )
                    }
                }
            }
    }

    fun updateUnreadNotifications(hasUnreadNotifications: Boolean) {
        hasUnreadNotificationsProcessor.offer(hasUnreadNotifications)
    }
}