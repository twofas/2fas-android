package com.twofasapp.base.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

class AppDispatchers : Dispatchers {
    override fun io(): CoroutineDispatcher {
        return kotlinx.coroutines.Dispatchers.IO
    }
}