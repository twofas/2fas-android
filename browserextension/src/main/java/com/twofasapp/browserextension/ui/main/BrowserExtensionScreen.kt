package com.twofasapp.browserextension.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.twofasapp.design.compose.ButtonShape
import com.twofasapp.design.compose.ButtonTextColor
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.RequestPermission
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.InputDialog
import com.twofasapp.designsystem.ktx.openSafely
import com.twofasapp.designsystem.screen.CommonContent
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.locale.TwLocale
import com.twofasapp.resources.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun BrowserExtensionScreen(
    openPairingScan: () -> Unit,
    openBrowserDetails: (String) -> Unit,
    viewModel: BrowserExtensionViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var askForPermission by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.browserExtTitle) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (uiState.isLoading) return@Scaffold

        if (uiState.pairedBrowsers.isEmpty()) {
            EmptyScreen(
                onPairBrowserClick = { askForPermission = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            )
        } else {
            ContentScreen(
                onBrowserClick = openBrowserDetails,
                onPairBrowserClick = { askForPermission = true },
                viewModel = viewModel,
                uiState = uiState,
                padding = padding,
            )
        }
    }

    if (askForPermission) {
        RequestPermission(
            permission = Manifest.permission.CAMERA,
            onGranted = {
                askForPermission = false
                openPairingScan()
            },
            onDismissRequest = { askForPermission = false },
            rationaleTitle = TwLocale.strings.permissionCameraTitle,
            rationaleText = TwLocale.strings.permissionCameraBody,
        )
    }

    uiState.getMostRecentEvent()?.let {
        when (it) {
            is BrowserExtensionUiState.Event.ShowSnackbarError -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(it.message)
                    viewModel.eventHandled(it.id)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ContentScreen(
    onBrowserClick: (String) -> Unit,
    onPairBrowserClick: () -> Unit,
    viewModel: BrowserExtensionViewModel,
    uiState: BrowserExtensionUiState,
    padding: PaddingValues,
) {
    val activity = LocalContext.current as Activity
    val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    LazyColumn(modifier = Modifier.padding(padding)) {
        item { SettingsHeader(TwLocale.strings.browserExtPairedDevices) }

        items(uiState.pairedBrowsers, key = { it.id }) {
            SimpleEntry(
                title = it.name,
                iconVisibleWhenNotSet = true,
                subtitle = TwLocale.formatDate(it.pairedAt),
                click = { onBrowserClick(it.id) }
            )
        }

        item {
            Button(
                onClick = { onPairBrowserClick() }, shape = ButtonShape(), modifier = Modifier.padding(start = 72.dp, top = 6.dp, bottom = 2.dp)
            ) {
                Text(text = stringResource(id = R.string.browser__add_new), color = ButtonTextColor())
            }
        }

        item { SettingsHeader(TwLocale.strings.browserExtDeviceName) }

        item {
            SimpleEntry(
                title = uiState.mobileDevice?.name.orEmpty(),
                subtitle = TwLocale.strings.browserExtDeviceNameSubtitle,
                iconEnd = painterResource(id = R.drawable.ic_toolbar_edit),
                iconEndClick = { viewModel.onEditDeviceClick() },
            )
        }

        if (permissionState.status.isGranted.not()) {
            item {
                Divider(Modifier.padding(top = 24.dp, bottom = 24.dp))
                Text(
                    text = stringResource(id = R.string.browser__push_notifications_content),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, color = TwTheme.color.primary),
                    modifier = Modifier.padding(start = 72.dp, bottom = 8.dp, end = 16.dp)
                )
            }
            item {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", activity.packageName, null))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        activity.startActivity(intent)
                    },
                    shape = ButtonShape(),
                    modifier = Modifier.padding(start = 72.dp, top = 6.dp, bottom = 2.dp)
                ) {
                    Text(text = "Enable Notifications", color = ButtonTextColor())
                }
            }
        }
    }

    if (uiState.showEditDeviceDialog) {
        InputDialog(
            onDismissRequest = { viewModel.onEditDeviceDialogDismiss() },
            prefill = uiState.mobileDevice?.name.orEmpty(),
            hint = stringResource(id = R.string.browser__device_name),
            positive = stringResource(id = R.string.commons__OK),
            negative = stringResource(id = R.string.commons__cancel),
            minLength = 1,
            maxLength = 100,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            onPositiveClick = { viewModel.onEditDeviceDialogDismiss(it) },
        )
//        InputDialo(
//            prefill = uiState.mobileDevice?.name.orEmpty(),
//            hint = stringResource(id = R.string.browser__device_name),
//            allowEmpty = false,
//            maxLength = 100,
//            onDismiss = { viewModel.onEditDeviceDialogDismiss() },
//            onPositive = { viewModel.onEditDeviceDialogDismiss(it) },
//        )
    }
}

@Composable
internal fun EmptyScreen(
    onPairBrowserClick: () -> Unit,
    modifier: Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    CommonContent(
        image = painterResource(id = R.drawable.browser_extension_start_image),
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

//
//
//    ConstraintLayout(
//        modifier = modifier
//    ) {
//        val (content, pair) = createRefs()
//
//        Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
//            .constrainAs(content) {
//                top.linkTo(parent.top)
//                bottom.linkTo(pair.top)
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//            }
//            .padding(vertical = 16.dp)) {
//            Image(
//                painter = painterResource(id = R.drawable.browser_extension_start_image),
//                contentDescription = null,
//                modifier = Modifier
//                    .height(130.dp)
//                    .offset(y = (-16).dp)
//            )
//
//            Text(
//                text = buildAnnotatedString {
//                    append("${TwLocale.strings.browserExtMore1} ")
//                    withStyle(style = SpanStyle(TwTheme.color.primary)) {
//                        append(TwLocale.strings.browserExtMore2)
//                    }
//                },
//                style = TwTheme.typo.body2,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .padding(horizontal = 16.dp)
//                    .padding(top = 16.dp)
//                    .clickable { uriHandler.openUri(TwLocale.links.browserExt) },
//            )
//
//            Text(
//                text = stringResource(id = R.string.browser__info_description_first) + "\n\n" + stringResource(id = R.string.browser__info_description_second),
//                style = MaterialTheme.typography.body1,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.padding(horizontal = 16.dp)
//            )
//
//            Text(
//                text = buildAnnotatedString {
//                    append(stringResource(id = R.string.browser__more_info) + " ")
//                    withStyle(style = SpanStyle(MaterialTheme.colors.primary)) {
//                        append(stringResource(id = R.string.browser__more_info_link_title))
//                    }
//                }, style = MaterialTheme.typography.body2, modifier = Modifier
//                    .padding(horizontal = 16.dp)
//                    .align(CenterHorizontally)
//                    .clickable {
//                        activity?.openBrowserApp(url = "https://2fas.com/be")
//                    }, textAlign = TextAlign.Center
//            )
//        }
//
//
//        Button(onClick = { onPairBrowserClick() }, shape = ButtonShape(), modifier = Modifier
//            .height(48.dp)
//            .constrainAs(pair) {
//                bottom.linkTo(parent.bottom, margin = 16.dp)
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//            }) {
//            Text(text = stringResource(id = R.string.browser__pair_with_web_browser).uppercase(), color = ButtonTextColor())
//        }
//    }
}