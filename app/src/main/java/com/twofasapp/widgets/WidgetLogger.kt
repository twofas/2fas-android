package com.twofasapp.widgets

import timber.log.Timber

object WidgetLogger {

    fun log(text: String) {
        Timber.tag("Widget").d(text)
    }
}