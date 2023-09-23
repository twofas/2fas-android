package com.twofasapp.feature.browserext.ui.permission

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.common.RequestPermission
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.screen.CommonContent
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BrowserExtPermissionScreen(
    viewModel: BrowserExtPermissionViewModel = koinViewModel(),
    openMain: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContent(
        uiState = uiState,
        onContinue = openMain,
    )
}

@Composable
private fun ScreenContent(
    uiState: BrowserExtPermissionUiState,
    onContinue: () -> Unit = {},
) {
    val strings = TwLocale.strings
    var askForPermission by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TwTopAppBar(strings.browserExtTitle) }
    ) { padding ->

        CommonContent(
            image = painterResource(id = R.drawable.illustration_push_notification),
            titleText = strings.browserPermissionTitle,
            descriptionText = strings.browserPermissionMsg,
            ctaPrimaryText = strings.browserPermissionCta,
            ctaPrimaryClick = { askForPermission = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        )
    }

    if (askForPermission) {
        RequestPermission(
            permission = Manifest.permission.POST_NOTIFICATIONS,
            onGranted = { onContinue() },
            onDismissRequest = {
                askForPermission = false
                onContinue()
            },
            rationaleTitle = TwLocale.strings.permissionPushTitle,
            rationaleText = TwLocale.strings.permissionPushBody,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ScreenContent(
        uiState = BrowserExtPermissionUiState()
    )
}
