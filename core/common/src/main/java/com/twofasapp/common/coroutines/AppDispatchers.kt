package com.twofasapp.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher

class AppDispatchers : Dispatchers {
    override val io: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.IO
}