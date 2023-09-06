package com.twofasapp.android.navigation

import androidx.navigation.NamedNavArgument

sealed class Screen(val route: String) {

    fun routeWithArgs(vararg args: Pair<NamedNavArgument, Any>): String {
        return route.replaceArgsInRoute(*args)
    }

    data object Guides : Screen("guides")
    data object GuideInit : Screen("guides/init?guide={${NavArg.Guide.name}}")
    data object GuidePager : Screen("guides/pager?guide={${NavArg.Guide.name}}&variant={${NavArg.GuideVariantIndex.name}}")
}