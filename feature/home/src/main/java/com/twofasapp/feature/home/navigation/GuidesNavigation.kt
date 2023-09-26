package com.twofasapp.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.Modal
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.Screen
import com.twofasapp.feature.home.ui.guideinit.GuideInitScreen
import com.twofasapp.feature.home.ui.guidepager.GuidePagerScreen
import com.twofasapp.feature.home.ui.guides.GuidesScreen


fun NavGraphBuilder.guidesNavigation(
    navController: NavHostController,
) {
    composable(Screen.Guides.route) {
        GuidesScreen(
            openGuide = { navController.navigate(Screen.GuideInit.routeWithArgs(NavArg.Guide to it.name)) }
        )
    }

    composable(Screen.GuideInit.route, listOf(NavArg.Guide)) {
        GuideInitScreen(
            guide = enumValueOf(it.arguments!!.getString(NavArg.Guide.name)!!),
            openGuide = { guide, guideVariantIndex ->
                navController.navigate(
                    Screen.GuidePager.routeWithArgs(
                        NavArg.Guide to guide.name,
                        NavArg.GuideVariantIndex to guideVariantIndex,
                    )
                )
            }
        )
    }

    composable(Screen.GuidePager.route, listOf(NavArg.Guide, NavArg.GuideVariantIndex)) {
        GuidePagerScreen(
            guide = enumValueOf(it.arguments!!.getString(NavArg.Guide.name)!!),
            guideVariantIndex = it.arguments!!.getInt(NavArg.GuideVariantIndex.name),
            openAddScan = {
                navController.popBackStack(Screen.Services.route, false)
                navController.navigate(Modal.AddService.route)
            },
            openAddManually = {
                navController.popBackStack(Screen.Services.route, false)
                navController.navigate(Modal.AddService.routeWithArgs(NavArg.AddServiceInitRoute to "manual"))
            },
        )
    }
}