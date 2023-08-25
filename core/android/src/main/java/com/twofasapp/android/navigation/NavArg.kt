package com.twofasapp.android.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

object NavArg {
    val AddServiceInitRoute = navArgument("AddServiceInitRoute") { type = NavType.StringType; nullable = true; defaultValue = null }
    val Guide = navArgument("Guide") { type = NavType.StringType; }
    val GuideVariantIndex = navArgument("GuideVariantIndex") { type = NavType.IntType; }
}