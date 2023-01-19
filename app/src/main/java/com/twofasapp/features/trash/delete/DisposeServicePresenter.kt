package com.twofasapp.features.trash.delete

import com.twofasapp.resources.R
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.services.domain.DeleteServiceUseCase

class DisposeServicePresenter(
    private val view: DisposeServiceContract.View,
    private val navigator: ScopedNavigator,
    private val deleteServiceUseCase: DeleteServiceUseCase,
) : DisposeServiceContract.Presenter() {

    override fun onViewAttached() {
        view.getServiceExtra().let {
            view.setHeader(it.name)
            view.setNote(R.string.tokens__you_will_not_be_able_to_sign_in_to_your, it.name)
        }

        view.closeClicks().safelySubscribe { navigator.navigateBack() }
        view.cancelClicks().safelySubscribe { navigator.navigateBack() }
        view.deleteSwitchChanges().safelySubscribe { view.setDeleteEnabled(it) }

        view.deleteClicks()
            .flatMapSingle { deleteServiceUseCase.execute(view.getServiceExtra()).toSingle { } }
            .safelySubscribe {
                navigator.finish()
            }
    }
}