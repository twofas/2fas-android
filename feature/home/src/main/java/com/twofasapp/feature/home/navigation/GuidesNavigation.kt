package com.twofasapp.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.LegacyScreen
import com.twofasapp.android.navigation.Modal
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.feature.home.ui.guideinit.GuideInitScreen
import com.twofasapp.feature.home.ui.guidepager.GuidePagerScreen
import com.twofasapp.feature.home.ui.guides.GuidesScreen

fun NavGraphBuilder.guidesNavigation(
    navController: NavHostController,
) {
    composable(LegacyScreen.Guides.route) {
        GuidesScreen(
            openGuide = { navController.navigate(LegacyScreen.GuideInit.routeWithArgs(NavArg.Guide to it.name)) },
        )
    }

    composable(LegacyScreen.GuideInit.route, listOf(NavArg.Guide)) {
        GuideInitScreen(
            guide = enumValueOf(it.arguments!!.getString(NavArg.Guide.name)!!),
            openGuide = { guide, guideVariantIndex ->
                navController.navigate(
                    LegacyScreen.GuidePager.routeWithArgs(
                        NavArg.Guide to guide.name,
                        NavArg.GuideVariantIndex to guideVariantIndex,
                    ),
                )
            },
        )
    }

    composable(LegacyScreen.GuidePager.route, listOf(NavArg.Guide, NavArg.GuideVariantIndex)) {
        GuidePagerScreen(
            guide = enumValueOf(it.arguments!!.getString(NavArg.Guide.name)!!),
            guideVariantIndex = it.arguments!!.getInt(NavArg.GuideVariantIndex.name),
            openAddScan = {
                navController.popBackStack(LegacyScreen.Services.route, false)
                navController.navigate(Modal.AddService.routeWithArgs())
            },
            openAddManually = {
                navController.popBackStack(LegacyScreen.Services.route, false)
                navController.navigate(Modal.AddService.routeWithArgs(NavArg.AddServiceInitRoute to "manual"))
            },
        )
    }
}