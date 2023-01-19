package com.twofasapp.base.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

interface Dispatchers {
    fun io(): CoroutineDispatcher
}