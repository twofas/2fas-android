package com.twofasapp.common.ktx

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

inline fun <T> runSafely(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (exception: Exception) {
        Result.failure(exception)
    }

fun tickerFlow(period: Long, initialDelay: Long = 0) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}