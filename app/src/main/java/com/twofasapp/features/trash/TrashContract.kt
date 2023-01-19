package com.twofasapp.features.trash

import com.mikepenz.fastadapter.IItem
import com.twofasapp.base.BasePresenter
import io.reactivex.Flowable

interface TrashContract {

    interface View {
        fun toolbarBackClicks(): Flowable<Unit>

        fun setItems(items: List<IItem<*>>)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
    }
}