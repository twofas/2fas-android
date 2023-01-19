package com.twofasapp.base

import java.util.*

abstract class UiEvent {
    val id: String = UUID.randomUUID().toString()
}