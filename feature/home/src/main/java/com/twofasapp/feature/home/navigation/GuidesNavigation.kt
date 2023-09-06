package com.twofasapp.feature.home.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.home.ui.guideinit.GuideInitScreen
import com.twofasapp.feature.home.ui.guidepager.GuidePagerScreen
import com.twofasapp.feature.home.ui.guides.Guide
import com.twofasapp.feature.home.ui.guides.GuidesScreen

@Composable
fun GuidesRoute(
    openGuide: (Guide) -> Unit,
) {
    GuidesScreen(
        openGuide = openGuide,
    )
}

@Composable
fun GuideInitRoute(
    guide: Guide,
    openGuide: (Guide, Int) -> Unit,
) {
    GuideInitScreen(
        guide = guide,
        openGuide = openGuide,
    )
}

@Composable
fun GuidePagerRoute(
    guide: Guide,
    guideVariantIndex: Int,
    openAddScan: () -> Unit,
    openAddManually: () -> Unit,
) {
    GuidePagerScreen(
        guide = guide,
        guideVariantIndex = guideVariantIndex,
        openAddScan = openAddScan,
        openAddManually = openAddManually,
    )
}