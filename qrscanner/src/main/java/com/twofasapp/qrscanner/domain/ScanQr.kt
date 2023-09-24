package com.twofasapp.qrscanner.domain

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class ScanQr {

    data class Result(
        val text: String,
        val isFromGallery: Boolean = false,
    )


    private val scanFlow = MutableSharedFlow<Result>(0, 1, BufferOverflow.DROP_OLDEST)
    private val resetFlow = MutableSharedFlow<Boolean>(0, 1, BufferOverflow.DROP_OLDEST)

    fun observeResultFlow() = scanFlow
    fun observeResetFlow() = resetFlow

    fun publishResult(result: Result) {
        scanFlow.tryEmit(result)
    }

    fun publishReset() {
        resetFlow.tryEmit(true)
    }
}
