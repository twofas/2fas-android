/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.logger

object Flog {
    @Volatile
    private var sinkLogcat: FlogSink? = null

    @Volatile
    private var sinkPersist: FlogSink? = null

    @Volatile
    private var debug: Boolean = false

    fun init(debug: Boolean, sinkLogcat: FlogSink, sinkPersist: FlogSink) {
        Flog.debug = debug
        Flog.sinkLogcat = sinkLogcat
        Flog.sinkPersist = sinkPersist
    }

    fun tag(tag: String): FlogTag = FlogTag(tag)

    fun v(message: String) = autoTag().v(message)
    fun d(message: String) = autoTag().d(message)
    fun i(message: String) = autoTag().i(message)
    fun w(message: String) = autoTag().w(message)
    fun e(message: String, throwable: Throwable? = null) = autoTag().e(message, throwable)
    fun e(throwable: Throwable?) = autoTag().e(throwable)

    fun persist(message: String) = persist("Info", message)
    fun persist(tag: String, message: String) = sinkPersist?.log(FlogLevel.Info, tag, message, null)
    fun persist(throwable: Throwable?) = persist("Error", throwable)
    fun persist(tag: String, throwable: Throwable?) = sinkPersist?.log(FlogLevel.Error, tag, "", throwable)

    internal fun dispatch(level: FlogLevel, tag: String, message: String, throwable: Throwable? = null) {
        sinkLogcat?.log(level, tag, message, throwable)
    }

    private fun autoTag(): FlogTag = FlogTag(callerTag())

    private fun callerTag(): String {
        if (!debug) return ""
        return Throwable().stackTrace
            .firstOrNull { it.className != Flog::class.java.name }
            ?.className
            ?.substringAfterLast('.')
            ?: ""
    }
}