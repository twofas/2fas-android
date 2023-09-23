package com.twofasapp.feature.browserext.ui.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.domain.PairedBrowser
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.RequestPermission
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwIcon
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.InputDialog
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.ktx.openSafely
import com.twofasapp.designsystem.screen.CommonContent
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.locale.TwLocale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.Instant

@Composable
internal fun BrowserExtScreen(
    viewModel: BrowserExtViewModel = koinViewModel(),
    openScan: () -> Unit = {},
    openDetails: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContent(
        uiState = uiState,
        openScan = openScan,
        openDetails = openDetails,
        onUpdateDeviceName = { viewModel.updateDeviceName(it) },
        onEventConsumed = { viewModel.consumeEvent(it) }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ScreenContent(
    uiState: BrowserExtUiState,
    openScan: () -> Unit = {},
    openDetails: (String) -> Unit = {},
    onUpdateDeviceName: (String) -> Unit = {},
    onEventConsumed: (BrowserExtUiEvent) -> Unit = {},
) {
    val activity = LocalContext.currentActivity
    val strings = TwLocale.strings
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var askForCameraPermission by remember { mutableStateOf(false) }
    var showEditDeviceNameDialog by remember { mutableStateOf(false) }
    val notificationsPermissionState = if (LocalInspectionMode.current) {
        // Dummy in preview mode
        PermissionStatus.Denied(false)
    } else {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS).status
    }

    uiState.events.firstOrNull()?.let {
        LaunchedEffect(Unit) {
            when (it) {
                BrowserExtUiEvent.ShowErrorSnackbar -> scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(strings.errorUnknown)
                }
            }
        }

        onEventConsumed(it)
    }

    Scaffold(
        topBar = { TwTopAppBar(titleText = strings.browserExtTitle) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        if (uiState.loading) return@Scaffold

        if (uiState.pairedBrowsers.isEmpty()) {
            Empty(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                onPairBrowserClick = { askForCameraPermission = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
            ) {
                item { SettingsHeader(strings.browserExtPairedDevices) }

                items(uiState.pairedBrowsers, key = { it.id }) {
                    SettingsLink(
                        title = it.name,
                        showEmptySpaceWhenNoIcon = true,
                        subtitle = TwLocale.formatDate(it.pairedAt),
                        onClick = { openDetails(it.id) },
                    )
                }
                item {
                    TwButton(
                        text = strings.browserExtAddNew,
                        modifier = Modifier.padding(start = 72.dp, top = 6.dp, bottom = 2.dp),
                        onClick = openScan,
                    )
                }

                item { SettingsHeader(strings.browserExtDeviceName) }

                item {
                    SettingsLink(
                        title = uiState.mobileDevice.name.orEmpty(),
                        subtitle = TwLocale.strings.browserExtDeviceNameSubtitle,
                        endContent = {
                            TwIcon(
                                painter = TwIcons.Edit,
                                tint = TwTheme.color.iconTint,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { showEditDeviceNameDialog = true },
                            )
                        },
                    )
                }

                if (notificationsPermissionState.isGranted.not()) {
                    item {
                        HorizontalDivider(Modifier.padding(top = 24.dp, bottom = 24.dp))
                        Text(
                            text = strings.permissionPushBody,
                            style = TwTheme.typo.body3,
                            modifier = Modifier.padding(start = 72.dp, bottom = 8.dp, end = 16.dp),
                            color = TwTheme.color.primary,
                        )
                    }
                    item {
                        TwButton(
                            text = "Enable Notifications",
                            modifier = Modifier.padding(start = 72.dp, top = 6.dp, bottom = 2.dp),
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", activity.packageName, null)
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                activity.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }

    if (askForCameraPermission) {
        RequestPermission(
            permission = Manifest.permission.CAMERA,
            onGranted = {
                askForCameraPermission = false
                openScan()
            },
            onDismissRequest = { askForCameraPermission = false },
            rationaleTitle = strings.permissionCameraTitle,
            rationaleText = strings.permissionCameraBody,
        )
    }

    if (showEditDeviceNameDialog) {
        InputDialog(
            onDismissRequest = { showEditDeviceNameDialog = false },
            prefill = uiState.mobileDevice.name.orEmpty(),
            hint = strings.browserExtDeviceName,
            positive = strings.commonOk,
            negative = strings.commonCancel,
            minLength = 1,
            maxLength = 100,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            onPositiveClick = { onUpdateDeviceName(it) },
        )
    }
}

@Composable
private fun Empty(
    modifier: Modifier = Modifier,
    onPairBrowserClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    CommonContent(
        image = painterResource(id = R.drawable.illustration_2fas_be),
        titleText = TwLocale.strings.browserExtHeader,
        descriptionText = "${TwLocale.strings.browserExtBody1}\n${TwLocale.strings.browserExtBody2}",
        ctaPrimaryText = TwLocale.strings.browserExtCta,
        ctaPrimaryClick = onPairBrowserClick,
        description = {
            Text(
                text = buildAnnotatedString {
                    append("${TwLocale.strings.browserExtMore1} ")
                    withStyle(style = SpanStyle(TwTheme.color.primary)) {
                        append(TwLocale.strings.browserExtMore2)
                    }
                },
                style = TwTheme.typo.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .clickable { uriHandler.openSafely(TwLocale.links.browserExt, context) },
            )
        },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PreviewEmpty() {
    ScreenContent(
        uiState = BrowserExtUiState(loading = false),
    )
}

@Preview
@Composable
private fun PreviewContent() {
    ScreenContent(
        uiState = BrowserExtUiState(
            loading = false,
            mobileDevice = MobileDevice(
                id = "",
                name = "Mobile Device",
                fcmToken = "",
                platform = "",
                publicKey = ""
            ),
            pairedBrowsers = listOf(
                PairedBrowser(
                    id = "",
                    name = "Paired Browser",
                    pairedAt = Instant.now(),
                    extensionPublicKey = "",
                )
            )
        )
    )
}
