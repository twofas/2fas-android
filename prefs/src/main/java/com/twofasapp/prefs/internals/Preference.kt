package com.twofasapp.prefs.internals

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class Preference<T> {
    protected abstract val key: String
    protected abstract val default: T
    protected var cached: T? = null
    protected val flow: MutableSharedFlow<T> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    abstract fun get(): T
    abstract fun put(model: T)
    abstract fun delete()
    abstract fun flow(emitOnSubscribe: Boolean = true): Flow<T>
}