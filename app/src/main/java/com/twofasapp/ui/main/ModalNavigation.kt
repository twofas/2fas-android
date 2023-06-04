package com.twofasapp.ui.main

sealed class ModalNavigation(val route: String) {

    object FocusService : ModalNavigation("modal/focusservice/{id}")
    object AddService : ModalNavigation("modal/addservice")
}