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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.twofasapp.designsystem.TwsTheme
import com.twofasapp.designsystem.composable.TwsPrimaryButton
import com.twofasapp.designsystem.composable.TwsTextButton
import com.twofasapp.feature.startup.R
import com.twofasapp.locale.TwsLocale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun StartupRoute(
    onFinish: () -> Unit,
    viewModel: StartupViewModel = koinViewModel()
) {
    StartupScreen(
        onTermsClick = { viewModel.onTermsClicked() },
        onNextClick = { viewModel.onNextClicked(it) },
        onStartUsingClick = {
            viewModel.onStartUsingClicked()
            onFinish()
        },
        onSkipClick = {
            viewModel.onSkipClicked()
            onFinish()
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
                        headerText = TwsLocale.strings.startupStepOneHeader,
                        bodyText = TwsLocale.strings.startupStepOneBody,
                        imageSize = 60.dp,
                    )

                    1 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_two),
                        headerText = TwsLocale.strings.startupStepTwoHeader,
                        bodyText = TwsLocale.strings.startupStepTwoBody,
                    )

                    2 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_three),
                        headerText = TwsLocale.strings.startupStepThreeHeader,
                        bodyText = TwsLocale.strings.startupStepThreeBody,
                    )

                    3 -> Step(
                        image = painterResource(id = R.drawable.onboarding_step_four),
                        headerText = TwsLocale.strings.startupStepFourHeader,
                        bodyText = TwsLocale.strings.startupStepFourBody,
                    )
                }
            }

            if (pagerState.currentPage == 0) {
                Text(
                    text = TwsLocale.strings.startupTermsLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TwsTheme.color.onSurfaceDarker,
                    modifier = Modifier
                        .clip(TwsTheme.shape.roundedDefault)
                        .clickable {
                            onTermsClick()
                            uriHandler.openUri(TwsLocale.links.terms)
                        }
                        .padding(4.dp)
                )
            } else {
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = TwsTheme.color.primary,
                    inactiveColor = TwsTheme.color.divider,
                    pageCount = pagerState.pageCount - 1,
                    pageIndexMapping = { it - 1 }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TwsPrimaryButton(
                text = when (pagerState.currentPage) {
                    1 -> TwsLocale.strings.commonNext
                    2 -> TwsLocale.strings.commonNext
                    3 -> TwsLocale.strings.startupStartCta
                    else -> TwsLocale.strings.commonContinue
                },
                modifier = Modifier.padding(vertical = 24.dp),
            ) {
                if (pagerState.currentPage == pagerState.pageCount - 1) {
                    onStartUsingClick()
                } else {
                    onNextClick(pagerState.currentPage)
                }

                scope.launch {
                    pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                }
            }
        }

        if (pagerState.currentPage != 0) {
            TwsTextButton(
                text = TwsLocale.strings.commonSkip,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                onSkipClick()
            }
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
