package com.twofasapp.core.design.foundation.lazy

abstract class ListItem(key: Any? = null, type: Any? = null) {
    val key: Any = key ?: javaClass
    val type: Any = type ?: javaClass
}