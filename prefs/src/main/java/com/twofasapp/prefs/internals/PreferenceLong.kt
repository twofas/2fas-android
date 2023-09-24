package com.twofasapp.prefs.internals

import com.twofasapp.storage.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription

abstract class PreferenceLong(
    private val preferences: Preferences
) : Preference<Long>() {

    override val default: Long = 0L

    override fun get(): Long =
        cached
            ?: preferences.getLong(key)?.also {
                cached = it
            }
            ?: default

    override fun put(model: Long) =
        preferences.putLong(key, model).also {
            cached = model
            flow.tryEmit(model)
        }

    override fun delete() = preferences.delete(key).also {
        cached = null
    }

    override fun flow(emitOnSubscribe: Boolean): Flow<Long> {
        return if (emitOnSubscribe) {
            flow.onSubscription { emit(get()) }
        } else {
            flow
        }
    }
}