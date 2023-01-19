package com.twofasapp.base

import timber.log.Timber

interface UiState<U, T : UiEvent> {
    val events: List<T>

    fun copyStateWithNewEvents(events: List<T>): U

    fun postEvent(event: T): U {
        Timber.tag("UiEvent")
        Timber.d("post :: ${event.id} :: ${event.javaClass.simpleName}")
        return copyStateWithNewEvents(events.plus(event))
    }

    fun reduceEvent(event: T): U {
        Timber.tag("UiEvent")
        Timber.d("reduce :: ${event.id} :: ${event.javaClass.simpleName} ")
        return copyStateWithNewEvents(events.filterNot { e -> e.id == event.id })
    }

    fun reduceEvent(id: String): U {
        Timber.tag("UiEvent")
        Timber.d("reduce :: $id")
        return copyStateWithNewEvents(events.filterNot { e -> e.id == id })
    }

    fun handleEvent(function: (event: T) -> Unit) {
        events.firstOrNull()?.let(function)
    }

    fun getMostRecentEvent(): T? {
        return events.firstOrNull()
    }
}