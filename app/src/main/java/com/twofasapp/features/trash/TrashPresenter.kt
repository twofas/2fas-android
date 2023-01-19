package com.twofasapp.features.trash

import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.usecases.services.GetTrashedServices
import com.twofasapp.usecases.services.RestoreService

class TrashPresenter(
    private val view: TrashContract.View,
    private val navigator: ScopedNavigator,
    private val getTrashedServices: GetTrashedServices,
    private val restoreService: RestoreService,
) : TrashContract.Presenter() {

    override fun onViewAttached() {
        view.toolbarBackClicks().safelySubscribe { navigator.navigateBack() }
        refreshItems()
    }

    override fun onResume() {
        refreshItems()
    }

    private fun refreshItems() {
        getTrashedServices.execute()
            .safelySubscribe { list ->

                if (list.isEmpty()) {
                    view.setItems(listOf(EmptyTrashItem()))
                    return@safelySubscribe
                }

                val items = list
                    .sortedByDescending { it.updatedAt }
                    .map { service ->
                        TrashedServiceItem(
                            model = TrashedService(service),
                            onRestoreClick = { onRestoreClick(it) },
                            onDeleteClick = { onDeleteClick(it) },
                        )
                    }

                view.setItems(items)
            }
    }

    private fun onRestoreClick(model: TrashedService) {
        restoreService.execute(model.service)
            .safelySubscribe { refreshItems() }
    }

    private fun onDeleteClick(model: TrashedService) {
        navigator.openDisposeService(model.service)
    }
}