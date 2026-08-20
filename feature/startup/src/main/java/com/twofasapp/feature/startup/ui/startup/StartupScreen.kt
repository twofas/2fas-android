package com.twofasapp.feature.startup.ui.startup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.openSafely
import com.twofasapp.feature.startup.R
import com.twofasapp.locale.MdtLocale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun StartupScreen(
    viewModel: StartupViewModel = koinViewModel(),
) {
    ScreenContent(
        onFinish = { viewModel.finishOnboarding(it) },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ScreenContent(
    onFinish: (openBackup: Boolean) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 5 })
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    val pageCount by remember { derivedStateOf { pagerState.pageCount } }

    fun scroll(page: Int) {
        scope.launch { pagerState.animateScrollToPage(page) }
    }

    BackHandler(
        enabled = currentPage > 0,
    ) {
        scroll(currentPage - 1)
    }

    Scaffold(
        topBar = { TopAppBar(showBackButton = currentPage > 0) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (page) {
                    0 -> WelcomeStep()

                    1 -> Step(
                        title = MdtLocale.strings.startupStepTwoHeader,
                        subtitle = MdtLocale.strings.startupStepTwoBody,
                        image = painterResource(id = R.drawable.onboarding_step_two),
                    )

                    2 -> Step(
                        title = MdtLocale.strings.startupStepThreeHeader,
                        subtitle = MdtLocale.strings.startupStepThreeBody,
                        image = painterResource(id = R.drawable.onboarding_step_three),
                    )

                    3 -> Step(
                        title = MdtLocale.strings.startupStepFourHeader,
                        subtitle = MdtLocale.strings.startupStepFourBody,
                        image = painterResource(id = R.drawable.onboarding_step_four),
                    )

                    4 -> Step(
                        title = MdtLocale.strings.startupBackupHeader,
                        subtitle = MdtLocale.strings.startupBackupBody,
                        image = painterResource(id = com.twofasapp.core.design.R.drawable.illustration_2fas_backup),
                        additionalContent = {
                            Space(24.dp)

                            Button(
                                text = MdtLocale.strings.startupBackupCloseCta,
                                style = ButtonStyle.Text,
                                contentColor = MdtTheme.color.onBackground,
                                onClick = { onFinish(false) },
                            )
                        },
                    )
                }
            }

            if (currentPage > 0) {
                Space(16.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    repeat(pagerState.pageCount - 1) { iteration ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage - 1 == iteration) {
                                        MdtTheme.color.primary
                                    } else {
                                        MdtTheme.color.surfaceContainerHighest
                                    },
                                )
                                .size(8.dp),
                        )
                    }
                }

                Space(24.dp)
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                text = when (currentPage) {
                    0, pageCount - 1 -> MdtLocale.strings.commonContinue
                    else -> MdtLocale.strings.commonNext
                },
                onClick = {
                    when (currentPage) {
                        pageCount - 1 -> onFinish(true)
                        else -> scroll(currentPage + 1)
                    }
                },
            )
        }
    }
}

@Composable
private fun WelcomeStep() {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Space(0.6f)

        Image(
            painter = painterResource(com.twofasapp.core.design.R.drawable.logo_auth),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Space(0.3f)

        Text(
            text = MdtLocale.strings.startupStepOneHeader,
            style = MdtTheme.typo.xl3.medium,
        )

        Space(16.dp)

        Text(
            text = MdtLocale.strings.startupStepOneBody,
            style = MdtTheme.typo.base.normal,
            color = MdtTheme.color.onSurfaceTertiary,
            textAlign = TextAlign.Center,
        )

        Space(16.dp)

        Button(
            text = MdtLocale.strings.startupTermsLabel,
            style = ButtonStyle.Text,
            contentColor = MdtTheme.color.onBackground,
            onClick = { uriHandler.openSafely(MdtLocale.links.terms) },
        )

        Space(1f)
    }
}

@Composable
private fun Step(
    title: String,
    subtitle: String,
    image: Painter,
    additionalContent: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Space(0.1f)

        Text(
            text = title,
            style = MdtTheme.typo.xl3.medium,
        )

        Space(16.dp)

        Text(
            text = subtitle,
            style = MdtTheme.typo.base.normal,
            color = MdtTheme.color.onSurfaceTertiary,
            textAlign = TextAlign.Center,
        )

        additionalContent()

        Space(1f)

        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(0.7f),
        )

        Space(1f)
    }
}

@Preview
@Composable
private fun PreviewWelcome() {
    PreviewTheme {
        WelcomeStep()
    }
}

@Preview
@Composable
private fun PreviewStep() {
    PreviewTheme {
        Step(
            title = MdtLocale.strings.startupStepTwoHeader,
            subtitle = MdtLocale.strings.startupStepTwoBody,
            image = painterResource(id = R.drawable.onboarding_step_two),
        )
    }
}