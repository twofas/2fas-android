package com.twofasapp.prefs.internals

import com.twofasapp.storage.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription

abstract class PreferenceBoolean(
    private val preferences: Preferences
) : Preference<Boolean>() {

    override val default: Boolean = false

    override fun get(): Boolean =
        cached
            ?: preferences.getBoolean(key)?.also {
                cached = it
            }
            ?: default

    override fun put(model: Boolean) =
        preferences.putBoolean(key, model).also {
            cached = model
            flow.tryEmit(model)
        }

    override fun delete() = preferences.delete(key).also {
        cached = null
    }

    override fun flow(emitOnSubscribe: Boolean): Flow<Boolean> {
        return if (emitOnSubscribe) {
            flow.onSubscription { emit(get()) }
        } else {
            flow
        }
    }
}