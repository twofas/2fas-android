package com.twofasapp.android.navigation

interface Navigator {
    fun navigate(screen: Screen)
    fun back(): Boolean
    fun resetTo(screen: Screen)
    fun popTo(screen: Screen, inclusive: Boolean = false)
}