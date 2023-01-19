package com.twofasapp.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface Dispatchers {
    val io: CoroutineDispatcher
}