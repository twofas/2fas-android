package com.twofasapp.base.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.twofasapp.base.BasePresenter
import com.twofasapp.base.LifecycleOperation
import com.twofasapp.base.logLifecycle

class PresenterLifecycle(
    private val ownerTag: String,
    private val presenter: BasePresenter,
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        logLifecycle(ownerTag, "${presenter.toString().split(".").takeLast(1)}", "onPresenterAttached", LifecycleOperation.IN)
        presenter.onViewAttached()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        presenter.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        presenter.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        logLifecycle(ownerTag, "${presenter.toString().split(".").takeLast(1)}", "onPresenterDetached", LifecycleOperation.OUT)
        presenter.onViewDetached()
    }
}