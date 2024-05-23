package com.twofasapp.feature.startup.ui.startup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.designsystem.ktx.openSafely
import com.twofasapp.feature.startup.R
import com.twofasapp.locale.TwLocale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun StartupScreen(
    openHome: () -> Unit = {},
    openBackup: () -> Unit = {},
    viewModel: StartupViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()

    ScreenContent(
        openHome = {
            scope.launch {
                viewModel.finishOnboarding()
                openHome()
            }
        },
        openBackup = {
            scope.launch {
                viewModel.finishOnboarding()
                openBackup()
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ScreenContent(
    openHome: () -> Unit = {},
    openBackup: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 5 })
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_one),
                        headerText = TwLocale.strings.startupStepOneHeader,
                        bodyText = TwLocale.strings.startupStepOneBody,
                        imageSize = 60.dp,
                        openHome = openHome,
                    )

                    1 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_two),
                        headerText = TwLocale.strings.startupStepTwoHeader,
                        bodyText = TwLocale.strings.startupStepTwoBody,
                        openHome = openHome,
                    )

                    2 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_three),
                        headerText = TwLocale.strings.startupStepThreeHeader,
                        bodyText = TwLocale.strings.startupStepThreeBody,
                        openHome = openHome,
                    )

                    3 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_four),
                        headerText = TwLocale.strings.startupStepFourHeader,
                        bodyText = TwLocale.strings.startupStepFourBody,
                        openHome = openHome,
                    )

                    4 -> Step(
                        image = painterResource(id = com.twofasapp.designsystem.R.drawable.illustration_2fas_backup),
                        headerText = null,
                        bodyText = TwLocale.strings.startupBackupBody,
                        showBackupSkip = true,
                        openHome = openHome,
                    )
                }
            }

            if (pagerState.currentPage == 0) {
                Text(
                    text = TwLocale.strings.startupTermsLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TwTheme.color.onSurfaceSecondary,
                    modifier = Modifier
                        .clip(TwTheme.shape.roundedDefault)
                        .clickable { uriHandler.openSafely(TwLocale.links.terms, context) }
                        .padding(4.dp)
                )
            } else {
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = TwTheme.color.primary,
                    inactiveColor = TwTheme.color.divider,
                    pageCount = pagerState.pageCount - 1,
                    pageIndexMapping = { it - 1 }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            TwButton(
                text = when (pagerState.currentPage) {
                    1 -> TwLocale.strings.commonNext
                    2 -> TwLocale.strings.commonNext
                    3 -> TwLocale.strings.commonNext
                    4 -> TwLocale.strings.commonContinue
                    else -> TwLocale.strings.commonContinue
                },
                onClick = {
                    if (pagerState.canScrollForward.not()) {
                        openBackup()
                    }

                    scope.launch {
                        pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Step(
    image: Painter,
    headerText: String?,
    bodyText: String,
    modifier: Modifier = Modifier,
    imageSize: Dp = 180.dp,
    showBackupSkip: Boolean = false,
    openHome: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.height(imageSize)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Header
            if (headerText != null) {
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body
            Text(
                text = bodyText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center
            )

            if (showBackupSkip) {
                Spacer(modifier = Modifier.height(16.dp))

                TwTextButton(
                    text = TwLocale.strings.startupBackupCloseCta,
                    onClick = openHome,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
