package com.twofasapp.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    fun <T : UiEvent> List<T>.handle(id: String): List<T> {
        return filterNot { event -> event.id == id }
    }

    suspend fun <T> runSafely(catch: suspend () -> Unit = {}, action: suspend () -> T) {
        try {
            action.invoke()
        } catch (e: Exception) {
            catch.invoke()
        }
    }
}