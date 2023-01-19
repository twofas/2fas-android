package com.twofasapp.core.log

import android.util.Log
import timber.log.Timber

object FileLogger {
    const val TAG = "2FAS"
    const val fileName = "logs.txt"

    fun log(msg: String) {
        Timber.tag(TAG).log(Log.INFO, msg)
    }

    fun logScreen(screen: String) {
        log("Screen: $screen")
    }
}