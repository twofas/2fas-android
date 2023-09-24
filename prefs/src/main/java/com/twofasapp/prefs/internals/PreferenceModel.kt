package com.twofasapp.prefs.internals

import com.twofasapp.storage.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.json.Json

abstract class PreferenceModel<T>(
    private val preferences: Preferences
) : Preference<T>() {

    protected abstract val serialize: (T) -> String
    protected abstract val deserialize: (String) -> T
    protected val jsonSerializer: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        coerceInputValues = true
    }
    protected open var useCache: Boolean = true

    override fun get(): T {
        return (if (useCache) cached else null)
            ?: preferences.getString(key)?.let { deserialize.invoke(it) }.also {
                if (useCache) {
                    cached = it
                }
            }
            ?: default
    }

    override fun put(model: T) =
        preferences.putString(key, serialize.invoke(model)).also {
            if (useCache) {
                cached = model
            }
            flow.tryEmit(model)
        }

    fun put(action: (currentValue: T) -> T) = put(action.invoke(get()))

    override fun delete() = preferences.delete(key).also {
        cached = null
    }

    override fun flow(emitOnSubscribe: Boolean): Flow<T> {
        return if (emitOnSubscribe) {
            flow.onSubscription { emit(get()) }
        } else {
            flow
        }
    }
}
