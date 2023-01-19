package com.twofasapp.features.trash.delete

import com.twofasapp.prefs.model.ServiceDto
import io.reactivex.Flowable

interface DisposeServiceContract {

    interface View {
        fun deleteClicks(): Flowable<Unit>
        fun cancelClicks(): Flowable<Unit>
        fun closeClicks(): Flowable<Unit>
        fun deleteSwitchChanges(): Flowable<Boolean>

        fun getServiceExtra(): ServiceDto
        fun setHeader(serviceName: String?)
        fun setNote(resId: Int?, serviceName: String?)
        fun setDeleteEnabled(isEnabled: Boolean)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter()
}
