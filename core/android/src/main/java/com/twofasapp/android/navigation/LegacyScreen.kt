package com.twofasapp.android.navigation

import androidx.navigation.NamedNavArgument

sealed class LegacyScreen(val route: String) {

    fun routeWithArgs(vararg args: Pair<NamedNavArgument, Any?>): String {
        return route.replaceArgsInRoute(*args)
    }

    data object Startup : LegacyScreen("startup")
    data object Services : LegacyScreen("services")
    data object EditService : LegacyScreen("services/{${NavArg.ServiceId.name}}")

    data object Notifications : LegacyScreen("notifications")
    data object Dispose : LegacyScreen("dispose/{${NavArg.ServiceId.name}}")

    data object Backup : LegacyScreen("backup?turnOnBackup={${NavArg.TurnOnBackup.name}}")

    data object Guides : LegacyScreen("guides")
    data object GuideInit : LegacyScreen("guides/init?guide={${NavArg.Guide.name}}")
    data object GuidePager : LegacyScreen("guides/pager?guide={${NavArg.Guide.name}}&variant={${NavArg.GuideVariantIndex.name}}")
}