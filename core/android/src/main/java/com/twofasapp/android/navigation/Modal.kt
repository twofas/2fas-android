package com.twofasapp.android.navigation

import androidx.navigation.NamedNavArgument

sealed class Modal(val route: String) {

    fun routeWithArgs(vararg args: Pair<NamedNavArgument, Any>): String {
        return route.replaceArgsInRoute(*args)
    }

    data object FocusService : Modal("modal/focusservice/{id}")
    data object AddService : Modal("modal/addservice?init={${NavArg.AddServiceInitRoute.name}}")
}