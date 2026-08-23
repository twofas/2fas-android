package com.twofasapp.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.LegacyScreen
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.feature.home.ui.guideinit.GuideInitScreen
import com.twofasapp.feature.home.ui.guidepager.GuidePagerScreen
import com.twofasapp.feature.home.ui.guides.Guide
import com.twofasapp.feature.home.ui.guides.GuidesScreen

fun NavGraphBuilder.guidesNavigation(
    navController: NavHostController,
) {
    composable(LegacyScreen.Guides.route) {
        GuidesScreen()
    }

    composable(LegacyScreen.GuideInit.route, listOf(NavArg.Guide)) {
        GuideInitScreen(
            guide = enumValueOf(it.arguments!!.getString(NavArg.Guide.name)!!),
        )
    }

    composable(LegacyScreen.GuidePager.route, listOf(NavArg.Guide, NavArg.GuideVariantIndex)) {
        GuidePagerScreen(
            guide = enumValueOf(it.arguments!!.getString(NavArg.Guide.name)!!),
            guideVariantIndex = it.arguments!!.getInt(NavArg.GuideVariantIndex.name),
        )
    }
}

@Composable
fun GuidesRoute() {
    GuidesScreen()
}

@Composable
fun GuideInitRoute(
    guide: String,
) {
    GuideInitScreen(guide = enumValueOf<Guide>(guide))
}

@Composable
fun GuidePagerRoute(
    guide: String,
    guideVariantIndex: Int,
) {
    GuidePagerScreen(
        guide = enumValueOf<Guide>(guide),
        guideVariantIndex = guideVariantIndex,
    )
}