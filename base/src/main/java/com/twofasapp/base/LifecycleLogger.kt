package com.twofasapp.base

import com.twofasapp.base.lifecycle.PresenterLifecycle
import timber.log.Timber

internal enum class LifecycleOperation { IN, OUT }

internal fun BaseActivityPresenter<*>.logLifecycle(event: String, operation: LifecycleOperation) {
    Timber.d("[${this.javaClass.simpleName}] ${getIndicator(operation)} $event")
}

internal fun BaseComponentActivity.logLifecycle(event: String, operation: LifecycleOperation) {
    Timber.d("[${this.javaClass.simpleName}] ${getIndicator(operation)} $event")
}

internal fun PresenterLifecycle.logLifecycle(tag: String, presenterTag: String, event: String, operation: LifecycleOperation) {
    Timber.d("[$tag] ${getIndicator(operation)} $event $presenterTag")
}

private fun getIndicator(operation: LifecycleOperation): String =
    when (operation) {
        LifecycleOperation.IN -> "=>"
        LifecycleOperation.OUT -> "<="
    }