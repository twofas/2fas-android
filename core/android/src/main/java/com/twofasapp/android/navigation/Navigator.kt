package com.twofasapp.android.navigation

interface Navigator {
    fun open(screen: Screen)
    fun back(): Boolean
    fun resetTo(screen: Screen)
    fun popTo(screen: Screen, inclusive: Boolean = false)

    companion object {
        val Stub: Navigator = object : Navigator {
            override fun open(screen: Screen) = Unit
            override fun back(): Boolean = true
            override fun resetTo(screen: Screen) = Unit
            override fun popTo(screen: Screen, inclusive: Boolean) = Unit
        }
    }
}