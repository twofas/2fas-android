package com.twofasapp.feature.home.ui.guidepager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.feature.home.ui.guideinit.PreviewGuide
import com.twofasapp.feature.home.ui.guides.Guide
import com.twofasapp.feature.home.ui.guides.GuideJson
import com.twofasapp.feature.home.ui.guides.getGuideJson
import com.twofasapp.feature.home.ui.guides.json
import com.twofasapp.locale.TwLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

@Composable
internal fun GuidePagerScreen(
    json: Json = koinInject(),
    servicesRepository: ServicesRepository = koinInject(),
    guide: Guide,
    guideVariantIndex: Int,
    openAddScan: () -> Unit,
    openAddManually: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var guideJson by remember { mutableStateOf<GuideJson?>(null) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            json.decodeFromString<GuideJson>(context.getGuideJson(guide.json)).also { guideJson = it }
        }
    }

    Scaffold(
        topBar = {
            TwTopAppBar(
                titleText = when (guide) {
                    Guide.Universal -> TwLocale.strings.guideUniversalTitle
                    else -> TwLocale.strings.guideTitle.format(guideJson?.serviceName ?: "")
                }
            )
        }
    ) { padding ->

        guideJson?.let { guideJson ->
            Content(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                steps = guideJson.flow.menu.items[guideVariantIndex].steps,
                openAddScan = openAddScan,
                openAddManually = {
                    servicesRepository.setManualGuideSelectedPrefill(it)
                    openAddManually()
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    steps: List<GuideJson.Step>,
    openAddScan: () -> Unit = {},
    openAddManually: (String?) -> Unit = {},
) {

    val scope = rememberCoroutineScope()
    val stepsCount = steps.size
    val pagerState = rememberPagerState(pageCount = { stepsCount })
    val isLastStep by remember { derivedStateOf { pagerState.currentPage == stepsCount - 1 } }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.1f))

                Image(
                    painter = painterResource(
                        id = when (steps[page].image) {
                            "web_url" -> com.twofasapp.designsystem.R.drawable.illustration_web_url
                            "web_account_1" -> com.twofasapp.designsystem.R.drawable.illustration_web_account_1
                            "web_menu" -> com.twofasapp.designsystem.R.drawable.illustration_web_menu
                            "2fas_type" -> com.twofasapp.designsystem.R.drawable.illustration_2fas_type
                            "phone_qr" -> com.twofasapp.designsystem.R.drawable.illustration_phone_qr
                            "gears" -> com.twofasapp.designsystem.R.drawable.illustration_gears
                            "web_phone" -> com.twofasapp.designsystem.R.drawable.illustration_web_phone
                            "retype" -> com.twofasapp.designsystem.R.drawable.illustration_retype
                            "web_button" -> com.twofasapp.designsystem.R.drawable.illustration_web_button
                            "push_notification" -> com.twofasapp.designsystem.R.drawable.illustration_push_notification
                            "account" -> com.twofasapp.designsystem.R.drawable.illustration_account
                            "app_button" -> com.twofasapp.designsystem.R.drawable.illustration_app_button
                            "secret_key" -> com.twofasapp.designsystem.R.drawable.illustration_secret_key
                            else -> com.twofasapp.designsystem.R.drawable.ic_placeholder
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.height(140.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = parseMarkdown(
                        markdown = steps[page].content,
                        typography = MaterialTheme.typography
                    ),
                    color = TwTheme.color.onSurfacePrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(stepsCount) { index ->
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) {
                                TwTheme.color.primary
                            } else {
                                TwTheme.color.divider
                            }
                        )
                        .size(8.dp)
                )
            }
        }

        TwButton(
            text = if (isLastStep) {
                steps[pagerState.currentPage].cta?.name.orEmpty()
            } else {
                TwLocale.strings.commonNext
            },
            modifier = Modifier.padding(16.dp),
            onClick = {
                if (isLastStep) {
                    when (steps[pagerState.currentPage].cta?.action) {
                        "open_scanner" -> openAddScan()
                        "open_manually" -> openAddManually(steps[pagerState.currentPage].cta?.data)
                        else -> Unit
                    }
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Content(
        modifier = Modifier
            .fillMaxSize()
            .background(TwTheme.color.background),
        steps = PreviewGuide.flow.menu.items.first().steps,
    )
}