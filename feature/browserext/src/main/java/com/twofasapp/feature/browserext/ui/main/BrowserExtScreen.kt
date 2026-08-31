package com.twofasapp.feature.browserext.ui.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.R
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPaddingFirst
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonHeight
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.dialog.BaseDialog
import com.twofasapp.core.design.foundation.dialog.InputDialog
import com.twofasapp.core.design.foundation.dialog.InputValidation
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.progress.CircularProgressIndicator
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.core.design.ktx.openSafely
import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.domain.PairedBrowser
import com.twofasapp.feature.permissions.PermissionStatus
import com.twofasapp.feature.permissions.RequestPermission
import com.twofasapp.feature.permissions.isGranted
import com.twofasapp.feature.permissions.rememberPermissionState
import com.twofasapp.locale.MdtLocale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.Instant

@Composable
internal fun BrowserExtScreen(
    viewModel: BrowserExtViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        openScan = { navigator.open(Screen.BrowserExtScan) },
        onForgetBrowser = { viewModel.forgetBrowser(it.id) },
        onUpdateDeviceName = { viewModel.updateDeviceName(it) },
        onEventConsumed = { viewModel.consumeEvent(it) },
    )
}

@Composable
private fun Content(
    uiState: BrowserExtUiState,
    openScan: () -> Unit = {},
    onForgetBrowser: (PairedBrowser) -> Unit = {},
    onUpdateDeviceName: (String) -> Unit = {},
    onEventConsumed: (BrowserExtUiEvent) -> Unit = {},
) {
    val activity = LocalContext.currentActivity
    val strings = MdtLocale.strings
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var askForCameraPermission by remember { mutableStateOf(false) }
    var showEditDeviceNameDialog by remember { mutableStateOf(false) }
    val notificationsPermissionState = if (LocalInspectionMode.current) {
        // Dummy in preview mode
        PermissionStatus.Denied(false)
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS).status
        } else {
            PermissionStatus.Granted
        }
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
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = if (uiState.pairedBrowsers.isEmpty()) null else strings.browserExtTitle) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        if (uiState.loading) return@Scaffold

        if (uiState.pairedBrowsers.isEmpty()) {
            Empty(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                onPairBrowserClick = { askForCameraPermission = true },
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                item {
                    OptionHeader(
                        text = strings.browserExtPairedDevices,
                        contentPadding = OptionHeaderContentPaddingFirst,
                    )
                }

                items(uiState.pairedBrowsers, key = { it.id }) {
                    PairedBrowserItem(
                        browser = it,
                        deleting = uiState.deletingBrowserIds.contains(it.id),
                        onForgetClick = { onForgetBrowser(it) },
                    )
                }
                item {
                    Button(
                        text = strings.browserExtAddNew,
                        size = ButtonHeight.Small,
                        leadingIcon = MdtIcons.Add,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        onClick = openScan,
                    )
                }

                item { OptionHeader(text = strings.browserExtDeviceName) }

                item {
                    DeviceNameItem(
                        name = uiState.mobileDevice.name,
                        subtitle = strings.browserExtDeviceNameSubtitle,
                        onEditClick = { showEditDeviceNameDialog = true },
                    )
                }

                if (notificationsPermissionState.isGranted.not()) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
                            color = MdtTheme.color.outlineVariant,
                        )
                        Text(
                            text = strings.permissionPushBody,
                            style = MdtTheme.typo.sm.normal,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, end = 16.dp),
                            color = MdtTheme.color.primary,
                        )
                    }
                    item {
                        Button(
                            text = "Enable Notifications",
                            size = ButtonHeight.Small,
                            leadingIcon = MdtIcons.Warning,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", activity.packageName, null),
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                activity.startActivity(intent)
                            },
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
            label = strings.browserExtDeviceName,
            title = strings.browserExtDeviceName,
            icon = MdtIcons.Mobile,
            prefill = uiState.mobileDevice.name,
            positive = strings.commonOk,
            negative = strings.commonCancel,
            validate = { if (it.trim().length in 1..100) InputValidation.Valid else InputValidation.Invalid(null) },
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            onPositive = { onUpdateDeviceName(it.trim()) },
        )
    }
}

@Composable
private fun PairedBrowserItem(
    modifier: Modifier = Modifier,
    browser: PairedBrowser,
    deleting: Boolean = false,
    onForgetClick: () -> Unit = {},
) {
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = MdtIcons.Extension,
            tint = MdtTheme.color.primary,
            modifier = Modifier.size(24.dp),
        )

        Space(16.dp)

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = browser.name,
                style = MdtTheme.typo.material.titleMedium,
                color = MdtTheme.color.onSurface,
            )

            Text(
                text = MdtLocale.formatDate(browser.pairedAt),
                style = MdtTheme.typo.material.bodyMedium,
                color = MdtTheme.color.onSurfaceVariant,
            )
        }

        Space(8.dp)

        IconButton(
            icon = MdtIcons.Delete,
            iconTint = MdtTheme.color.outline,
            onClick = { showConfirmDeleteDialog = true },
        )
    }

    if (showConfirmDeleteDialog) {
        ForgetBrowserDialog(
            deleting = deleting,
            onDismissRequest = { showConfirmDeleteDialog = false },
            onConfirm = onForgetClick,
        )
    }
}

@Composable
private fun DeviceNameItem(
    modifier: Modifier = Modifier,
    name: String?,
    subtitle: String,
    onEditClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEditClick() }
            .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = MdtIcons.Mobile,
            tint = MdtTheme.color.primary,
            modifier = Modifier.size(24.dp),
        )

        Space(16.dp)

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = name.orEmpty(),
                style = MdtTheme.typo.material.titleMedium,
                color = MdtTheme.color.onSurface,
            )

            Text(
                text = subtitle,
                style = MdtTheme.typo.material.bodyMedium,
                color = MdtTheme.color.onSurfaceVariant,
            )
        }

        Space(8.dp)

        IconButton(
            icon = MdtIcons.Edit,
            iconTint = MdtTheme.color.outline,
            onClick = onEditClick,
        )
    }
}

@Composable
private fun ForgetBrowserDialog(
    deleting: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    val strings = MdtLocale.strings

    BaseDialog(
        onDismissRequest = { if (deleting.not()) onDismissRequest() },
        title = strings.browserDetailsForgetTitle,
        body = strings.browserDetailsForgetMsg,
        icon = MdtIcons.Warning,
        positive = if (deleting) null else strings.commonYes,
        negative = if (deleting) null else strings.commonNo,
        onPositiveClick = onConfirm,
        dismissOnPositive = false,
        properties = DialogProperties(
            dismissOnBackPress = deleting.not(),
            dismissOnClickOutside = deleting.not(),
        ),
        content = {
            if (deleting) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        },
    )
}

@Composable
private fun Empty(
    modifier: Modifier = Modifier,
    onPairBrowserClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = MdtLocale.strings.browserExtHeader,
            style = MdtTheme.typo.xl2.medium,
            textAlign = TextAlign.Center,
        )

        Space(24.dp)

        Text(
            text = "${MdtLocale.strings.browserExtBody1}\n${MdtLocale.strings.browserExtBody2}",
            style = MdtTheme.typo.base.normal,
            color = MdtTheme.color.onSurface,
            textAlign = TextAlign.Center,
        )

        Space(24.dp)

        Text(
            text = buildAnnotatedString {
                append("${MdtLocale.strings.browserExtMore1} ")
                withStyle(style = SpanStyle(MdtTheme.color.primary)) {
                    append(MdtLocale.strings.browserExtMore2)
                }
            },
            style = MdtTheme.typo.base.normal,
            color = MdtTheme.color.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { uriHandler.openSafely(MdtLocale.links.browserExt, context) },
        )

        Space(0.3f)

        Image(
            painter = painterResource(id = R.drawable.illustration_2fas_be),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(0.7f),
        )

        Space(1f)

        Button(
            modifier = Modifier.fillMaxWidth(),
            text = MdtLocale.strings.browserExtCta,
            onClick = onPairBrowserClick,
        )
    }
}

@Preview
@Composable
private fun PreviewEmpty() {
    PreviewTheme {
        Content(
            uiState = BrowserExtUiState(loading = false),
        )
    }
}

@Preview
@Composable
private fun PreviewContent() {
    PreviewTheme {
        Content(
            uiState = BrowserExtUiState(
                loading = false,
                mobileDevice = MobileDevice(
                    id = "",
                    name = "Mobile Device",
                    fcmToken = "",
                    platform = "",
                    publicKey = "",
                ),
                pairedBrowsers = listOf(
                    PairedBrowser(
                        id = "1",
                        name = "Paired Browser 1",
                        pairedAt = Instant.now(),
                        extensionPublicKey = "",
                    ),
                    PairedBrowser(
                        id = "2",
                        name = "Paired Browser 2",
                        pairedAt = Instant.now(),
                        extensionPublicKey = "",
                    ),
                ),
            ),
        )
    }
}