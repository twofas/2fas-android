package com.twofasapp.feature.startup.ui

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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.designsystem.ktx.openSafely
import com.twofasapp.feature.startup.R
import com.twofasapp.locale.TwLocale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun StartupRoute(
    openHome: () -> Unit,
    viewModel: StartupViewModel = koinViewModel()
) {
    StartupScreen(
        onTermsClick = { viewModel.onTermsClicked() },
        onNextClick = { viewModel.onNextClicked(it) },
        onStartUsingClick = {
            viewModel.onStartUsingClicked()
            openHome()
        },
        onSkipClick = {
            viewModel.onSkipClicked()
            openHome()
        },
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun StartupScreen(
    onTermsClick: () -> Unit,
    onNextClick: (Int) -> Unit,
    onStartUsingClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
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
                count = 4,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_one),
                        headerText = TwLocale.strings.startupStepOneHeader,
                        bodyText = TwLocale.strings.startupStepOneBody,
                        imageSize = 60.dp,
                    )

                    1 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_two),
                        headerText = TwLocale.strings.startupStepTwoHeader,
                        bodyText = TwLocale.strings.startupStepTwoBody,
                    )

                    2 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_three),
                        headerText = TwLocale.strings.startupStepThreeHeader,
                        bodyText = TwLocale.strings.startupStepThreeBody,
                    )

                    3 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_four),
                        headerText = TwLocale.strings.startupStepFourHeader,
                        bodyText = TwLocale.strings.startupStepFourBody,
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
                        .clickable {
                            onTermsClick()
                            uriHandler.openSafely(TwLocale.links.terms, context)
                        }
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

            Spacer(modifier = Modifier.height(12.dp))

            TwButton(
                text = when (pagerState.currentPage) {
                    1 -> TwLocale.strings.commonNext
                    2 -> TwLocale.strings.commonNext
                    3 -> TwLocale.strings.startupStartCta
                    else -> TwLocale.strings.commonContinue
                },
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = {
                    if (pagerState.currentPage == pagerState.pageCount - 1) {
                        onStartUsingClick()
                    } else {
                        onNextClick(pagerState.currentPage)
                    }

                    scope.launch {
                        pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                    }
                }

            )
        }

        if (pagerState.currentPage != 0) {
            TwTextButton(
                text = TwLocale.strings.commonSkip,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                onClick = { onSkipClick() }
            )
        }
    }
}

@Composable
private fun Step(
    image: Painter,
    headerText: String,
    bodyText: String,
    modifier: Modifier = Modifier,
    imageSize: Dp = 220.dp,
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
            Text(
                text = headerText,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

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
        }
    }
}
