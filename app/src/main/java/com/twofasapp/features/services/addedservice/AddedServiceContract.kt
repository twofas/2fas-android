package com.twofasapp.features.services.addedservice

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.entity.ServiceModel
import io.reactivex.Flowable

interface AddedServiceContract {

    interface View {
        fun customizeClicks(): Flowable<Unit>
        fun refreshCounterClicks(): Flowable<Unit>
        fun editIconClicks(): Flowable<Unit>
        fun getServiceExtra(): ServiceDto

        fun setTitle(text: String)
        fun setSubtitle(text: String)
        fun updateService(model: ServiceModel)
        fun closeBottomSheet()
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter()
}