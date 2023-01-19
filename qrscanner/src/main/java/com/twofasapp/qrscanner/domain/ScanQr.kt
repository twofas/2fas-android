package com.twofasapp.qrscanner.domain

import io.reactivex.processors.PublishProcessor
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class ScanQr {

    data class Result(
        val text: String,
        val isFromGallery: Boolean = false,
    )

    private val scanProcessor: PublishProcessor<Result> = PublishProcessor.create()
    private val resetProcessor: PublishProcessor<Boolean> = PublishProcessor.create()

    private val scanFlow = MutableSharedFlow<Result>(0, 1, BufferOverflow.DROP_OLDEST)
    private val resetFlow = MutableSharedFlow<Boolean>(0, 1, BufferOverflow.DROP_OLDEST)

    fun observeResult() = scanProcessor
    fun observeResultFlow() = scanFlow

    fun observeReset() = resetProcessor
    fun observeResetFlow() = resetFlow

    fun publishResult(result: Result) {
        scanProcessor.offer(result)
        scanFlow.tryEmit(result)
    }

    fun publishReset() {
        resetProcessor.offer(true)
        resetFlow.tryEmit(true)
    }
}
