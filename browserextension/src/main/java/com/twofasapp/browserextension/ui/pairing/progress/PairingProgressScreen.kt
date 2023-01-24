package com.twofasapp.browserextension.ui.pairing.progress

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.twofasapp.design.compose.AnimatedContent
import com.twofasapp.design.compose.ButtonHeight
import com.twofasapp.design.compose.ButtonShape
import com.twofasapp.design.compose.ButtonTextColor
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.resources.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun PairingProgressScreen(
    openMain: () -> Unit,
    openPairingScan: () -> Unit,
    extensionId: String,
    viewModel: PairingProgressViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()
    viewModel.pairBrowser(extensionId)

    Scaffold(
        topBar = {
            TwTopAppBar(titleText = stringResource(id = if (uiState.value.isPairing) R.string.browser__pairing_with_browser else R.string.settings__browser_extension_result_toolbar_title))
        }
    ) { padding ->
        AnimatedContent(
            condition = uiState.value.isPairing,
            contentWhenTrue = { ProgressContent() },
            contentWhenFalse = {
                ResultContent(
                    onContinueClick = { openMain() },
                    onScanAgainClick = { openPairingScan() },
                    uiState.value.isPairingSuccess,
                    uiState.value.code,
                    padding
                )
            },
        )
    }
}

@Composable
internal fun ProgressContent() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.browser_extension_progress))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    LottieAnimation(composition, progress)
}

@Composable
internal fun ResultContent(
    onContinueClick: () -> Unit = {},
    onScanAgainClick: () -> Unit = {},
    isSuccess: Boolean,
    code: Int? = null,
    padding: PaddingValues,
) {
    val image = if (isSuccess) R.drawable.browser_extension_success_image else R.drawable.browser_extension_error_image

    val title = when {
        isSuccess && code == 409 -> R.string.browser__already_paired_title
        isSuccess -> R.string.browser__pairing_successful_title
        else -> R.string.browser__pairing_failed_title
    }

    val description = when {
        isSuccess && code == 409 -> R.string.browser__result_error_browser_paired
        isSuccess -> R.string.browser_extension_result_success_description
        else -> R.string.browser__pairing_failed_description
    }

    val cta = if (isSuccess) R.string.commons__continue else R.string.browser__result_error_cta

    val ctaAction: () -> Unit = {
        if (isSuccess) {
            onContinueClick()
//        { router.navigate(SettingsDirections.GoBack) }
        } else {
            onScanAgainClick()
//            { router.navigate(SettingsDirections.PairingScan) }
        }
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxHeight()
            .padding(padding)
            .padding(horizontal = 16.dp)
    ) {
        val (content, pair) = createRefs()

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(pair.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(vertical = 16.dp)
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier
                    .height(130.dp)
                    .offset(y = (-16).dp)
            )

            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

            Text(
                text = stringResource(id = description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
        }

        Button(onClick = { ctaAction.invoke() },
            shape = ButtonShape(),
            modifier = Modifier
                .height(ButtonHeight())
                .constrainAs(pair) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            Text(text = stringResource(id = cta).uppercase(), color = ButtonTextColor())
        }
    }
}