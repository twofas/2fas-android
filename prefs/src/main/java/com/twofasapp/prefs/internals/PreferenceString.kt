package com.twofasapp.prefs.internals

import com.twofasapp.storage.Preferences
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription

abstract class PreferenceString(
    private val preferences: Preferences
) : Preference<String>() {

    override fun get(): String =
        cached
            ?: preferences.getString(key)?.also {
                cached = it
            }
            ?: default

    override fun put(model: String) =
        preferences.putString(key, model).also {
            cached = model
            processor.offer(model)
            flow.tryEmit(model)
        }

    override fun delete() = preferences.delete(key).also {
        cached = null
    }

    override fun observe(emitOnSubscribe: Boolean): Flowable<String> {
        return if (emitOnSubscribe) {
            processor.startWith(get())
        } else {
            processor
        }
    }

    override fun flow(emitOnSubscribe: Boolean): Flow<String> {
        return if (emitOnSubscribe) {
            flow.onSubscription { emit(get()) }
        } else {
            flow
        }
    }
}