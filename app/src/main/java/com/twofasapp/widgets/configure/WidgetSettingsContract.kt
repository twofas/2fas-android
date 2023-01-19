package com.twofasapp.widgets.configure

import com.mikepenz.fastadapter.IItem
import com.twofasapp.base.BasePresenter
import io.reactivex.Flowable

interface WidgetSettingsContract {

    interface View {
        fun toolbarBackClicks(): Flowable<Unit>
        fun saveClicks(): Flowable<Unit>
        fun cancelClicks(): Flowable<Unit>
        fun agreeClicks(): Flowable<Unit>
        fun getAppWidgetId(): Int
        fun isNew(): Boolean

        fun showContent()
        fun setItems(items: List<IItem<*>>)
        fun finishAddingWidget()
        fun close()
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter()
}