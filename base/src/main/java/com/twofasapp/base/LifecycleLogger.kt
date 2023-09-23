package com.twofasapp.base

import timber.log.Timber

internal enum class LifecycleOperation { IN, OUT }

internal fun BaseComponentActivity.logLifecycle(event: String, operation: LifecycleOperation) {
    Timber.d("[${this.javaClass.simpleName}] ${getIndicator(operation)} $event")
}

private fun getIndicator(operation: LifecycleOperation): String =
    when (operation) {
        LifecycleOperation.IN -> "=>"
        LifecycleOperation.OUT -> "<="
    }