package com.twofasapp.feature.guides.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.guides.ui.guideinit.GuideInitScreen
import com.twofasapp.feature.guides.ui.guidepager.GuidePagerScreen
import com.twofasapp.feature.guides.ui.guides.Guide
import com.twofasapp.feature.guides.ui.guides.GuidesScreen

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