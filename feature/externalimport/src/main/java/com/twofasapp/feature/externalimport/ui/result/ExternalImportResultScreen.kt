package com.twofasapp.feature.externalimport.ui.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwCircularProgressIndicator
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.toastShort
import com.twofasapp.designsystem.screen.CommonContent
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.externalimport.domain.image
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ExternalImportResultScreen(
    viewModel: ExternalImportResultViewModel = koinViewModel(),
    openSettings: () -> Unit,
    openImport: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val strings = TwLocale.strings

    LaunchedEffect(uiState.finishSuccess) {
        if (uiState.finishSuccess) {
            context.toastShort(strings.externalImportSuccessToast)
            openSettings()
        }
    }

    ScreenContent(
        uiState = uiState,
        onImport = { viewModel.importServices() },
        onTryAgain = openImport,
    )
}

@Composable
private fun ScreenContent(
    uiState: ExternalImportResultUiState,
    onImport: () -> Unit = {},
    onTryAgain: () -> Unit = {},
) {
    val strings = TwLocale.strings

    Scaffold(
        topBar = { TwTopAppBar(strings.externalImportResultTitle) }
    ) { padding ->

        AnimatedVisibility(
            visible = uiState.loading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                TwCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        AnimatedVisibility(
            visible = uiState.loading.not(),
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
                importType = uiState.importType,
                readResult = uiState.readResult,
                onImport = onImport,
                onTryAgain = onTryAgain,
            )
        }
    }
}

@Composable
private fun Result(
    modifier: Modifier = Modifier,
    importType: ImportType,
    readResult: ReadResult,
    onImport: () -> Unit,
    onTryAgain: () -> Unit,
) {
    val strings = TwLocale.strings

    val title = when (importType) {
        ImportType.GoogleAuthenticator -> strings.externalImportResultGoogleAuthenticatorTitle
        ImportType.Aegis -> strings.externalImportResultAegisTitle
        ImportType.Raivo -> strings.externalImportResultRaivoTitle
        ImportType.LastPass -> strings.externalImportResultLastPassTitle
        ImportType.AuthenticatorPro -> strings.externalImportResultAuthenticatorProTitle
        ImportType.AndOtp -> strings.externalImportResultAndOtpTitle
    }

    val description = when (readResult) {
        is ReadResult.Success -> {
            when (importType) {
                ImportType.GoogleAuthenticator -> strings.externalImportResultSuccessGoogleAuthenticatorMsg
                ImportType.Aegis -> strings.externalImportResultSuccessAegisMsg
                ImportType.Raivo -> strings.externalImportResultSuccessRaivoMsg
                ImportType.LastPass -> strings.externalImportResultSuccessLastPassMsg
                ImportType.AuthenticatorPro -> strings.externalImportResultSuccessAuthenticatorProMsg
                ImportType.AndOtp -> strings.externalImportResultSuccessAndOtpMsg
            }
        }

        ReadResult.Failure -> strings.externalImportResultErrorMsg
    }

    val additionalDescription: @Composable (() -> Unit)? = when (readResult) {
        is ReadResult.Success -> {
            {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = if (readResult.countServicesToImport == readResult.countTotalServices) {
                            readResult.countServicesToImport.toString()
                        } else {
                            strings.externalImportResultTokensCount.format(
                                readResult.countServicesToImport,
                                readResult.countTotalServices
                            )
                        },
                        style = TwTheme.typo.h3,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Text(
                        text = strings.externalImportResultTokensMsg,
                        style = TwTheme.typo.body1,
                    )
                }
            }
        }

        ReadResult.Failure -> null
    }

    val cta = when (readResult) {
        is ReadResult.Success -> strings.commonProceed
        ReadResult.Failure -> strings.commonTryAgain
    }

    val ctaAction = when (readResult) {
        is ReadResult.Success -> onImport
        ReadResult.Failure -> onTryAgain
    }

    CommonContent(
        image = painterResource(id = importType.image),
        titleText = title,
        descriptionText = description,
        description = additionalDescription,
        ctaPrimaryText = cta,
        ctaPrimaryClick = ctaAction,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PreviewSuccess() {
    ScreenContent(
        uiState = ExternalImportResultUiState(
            loading = false,
            readResult = ReadResult.Success(services = emptyList(), countServicesToImport = 5, countTotalServices = 10)
        )
    )
}

@Preview
@Composable
private fun PreviewFailure() {
    ScreenContent(
        uiState = ExternalImportResultUiState(
            loading = false,
            readResult = ReadResult.Failure
        )
    )
}
