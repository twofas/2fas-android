package com.twofasapp.navigation.base

abstract class ActivityRouter<T : Directions> {
    abstract fun navigateBack()
    abstract fun navigate(direction: T)
}