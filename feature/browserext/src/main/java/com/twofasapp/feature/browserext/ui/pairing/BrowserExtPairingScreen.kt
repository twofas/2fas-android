package com.twofasapp.feature.browserext.ui.pairing

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.notificationManager
import com.twofasapp.designsystem.screen.CommonContent
import com.twofasapp.feature.browserext.R
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BrowserExtPairingScreen(
    viewModel: BrowserExtPairingViewModel = koinViewModel(),
    openMain: () -> Unit,
    openPermission: () -> Unit,
    openScan: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContent(
        uiState = uiState,
        onContinue = openMain,
        onContinueAskForPermission = openPermission,
        onScanAgain = openScan,
    )
}

@Composable
private fun ScreenContent(
    uiState: BrowserExtPairingUiState,
    onContinue: () -> Unit = {},
    onContinueAskForPermission: () -> Unit = {},
    onScanAgain: () -> Unit = {},
) {
    val strings = TwLocale.strings

    Scaffold(
        topBar = { TwTopAppBar(if (uiState.pairing) strings.browserPairingTitle else strings.browserPairingResultTitle) }
    ) { padding ->

        AnimatedVisibility(
            visible = uiState.pairing,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Pairing(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        AnimatedVisibility(
            visible = uiState.pairing.not(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Result(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                pairingResult = uiState.pairingResult,
                onContinue = onContinue,
                onContinueAskForPermission = onContinueAskForPermission,
                onScanAgain = onScanAgain,
            )
        }
    }
}

@Composable
private fun Pairing(modifier: Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.browserext_progress))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    LottieAnimation(composition, progress, modifier = modifier)
}

@Composable
private fun Result(
    modifier: Modifier = Modifier,
    pairingResult: PairingResult,
    onContinue: () -> Unit,
    onContinueAskForPermission: () -> Unit,
    onScanAgain: () -> Unit,
) {
    val notificationManager = LocalContext.current.notificationManager
    val shouldAskForNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        notificationManager.areNotificationsEnabled().not()
    } else {
        false
    }


    val image = when (pairingResult) {
        PairingResult.Success -> com.twofasapp.designsystem.R.drawable.illustration_be_pairing_success
        PairingResult.Failure -> com.twofasapp.designsystem.R.drawable.illustration_be_pairing_error
        PairingResult.AlreadyPaired -> com.twofasapp.designsystem.R.drawable.illustration_be_pairing_success
    }

    val title = when (pairingResult) {
        PairingResult.Success -> TwLocale.strings.browserPairingSuccessTitle
        PairingResult.Failure -> TwLocale.strings.browserPairingFailureTitle
        PairingResult.AlreadyPaired -> TwLocale.strings.browserPairingAlreadyPairedTitle
    }

    val description = when (pairingResult) {
        PairingResult.Success -> TwLocale.strings.browserPairingSuccessMsg
        PairingResult.Failure -> TwLocale.strings.browserPairingFailureMsg
        PairingResult.AlreadyPaired -> TwLocale.strings.browserPairingAlreadyPairedMsg
    }

    val cta = when (pairingResult) {
        PairingResult.Success -> TwLocale.strings.browserPairingSuccessCta
        PairingResult.Failure -> TwLocale.strings.browserPairingFailureCta
        PairingResult.AlreadyPaired -> TwLocale.strings.browserPairingSuccessCta
    }

    val ctaAction = when (pairingResult) {
        PairingResult.Failure -> onScanAgain
        PairingResult.Success,
        PairingResult.AlreadyPaired -> {
            if (shouldAskForNotificationPermission) {
                onContinueAskForPermission
            } else {
                onContinue
            }
        }
    }

    CommonContent(
        image = painterResource(id = image),
        titleText = title,
        descriptionText = description,
        ctaPrimaryText = cta,
        ctaPrimaryClick = ctaAction,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PreviewParing() {
    ScreenContent(
        uiState = BrowserExtPairingUiState(
            pairing = true,
        )
    )
}

@Preview
@Composable
private fun PreviewSuccess() {
    ScreenContent(
        uiState = BrowserExtPairingUiState(
            pairing = false,
            pairingResult = PairingResult.Success
        )
    )
}

@Preview
@Composable
private fun PreviewFailure() {
    ScreenContent(
        uiState = BrowserExtPairingUiState(
            pairing = false,
            pairingResult = PairingResult.Failure
        )
    )
}

@Preview
@Composable
private fun PreviewAlreadyPaired() {
    ScreenContent(
        uiState = BrowserExtPairingUiState(
            pairing = false,
            pairingResult = PairingResult.AlreadyPaired
        )
    )
}
