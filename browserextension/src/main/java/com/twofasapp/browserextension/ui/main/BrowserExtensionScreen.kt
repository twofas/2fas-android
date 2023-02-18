package com.twofasapp.browserextension.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.twofasapp.design.compose.ButtonShape
import com.twofasapp.design.compose.ButtonTextColor
import com.twofasapp.design.compose.HeaderEntry
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.compose.dialogs.InputDialog
import com.twofasapp.design.compose.dialogs.RationaleDialog
import com.twofasapp.extensions.openBrowserApp
import com.twofasapp.navigation.SettingsDirections
import com.twofasapp.navigation.SettingsRouter
import com.twofasapp.resources.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun RequestPermission(
    permission: String,
    rationaleTitle: String = "",
    rationaleText: String = "",
    onGranted: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    var showRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            showRationale = true
        }
    }

    val permissionCheckResult = ContextCompat.checkSelfPermission(LocalContext.current, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        onGranted()
    } else {
        LaunchedEffect(Unit) {
            launcher.launch(permission)
        }
    }

    if (showRationale) {
        RationaleDialog(
            title = rationaleTitle, text = rationaleText, onDismiss = onDismiss
        )
    }
}

@Composable
internal fun BrowserExtensionScreen(
    viewModel: BrowserExtensionViewModel = get(),
    router: SettingsRouter = get()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var askForPermission by remember { mutableStateOf(false) }

    viewModel.onPairClick = { openPairingScan() }
    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.browserExtTitle) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (uiState.isLoading) return@Scaffold

        if (uiState.pairedBrowsers.isEmpty()) {
            EmptyScreen(router = router, padding = padding, onAddClick = { askForPermission = true })
        } else {
            ContentScreen(
                onBrowserClick = openBrowserDetails,
                onPairBrowserClick = openPairingScan,
                viewModel = viewModel,
                uiState = uiState,
                router = router,
                onAddClick = { askForPermission = true },
                padding = padding,
            )
        }
    }

    if (askForPermission) {
        RequestPermission(
            permission = Manifest.permission.CAMERA,
            onGranted = { router.navigate(SettingsDirections.PairingScan) },
            onDismiss = { askForPermission = false },
            rationaleTitle = stringResource(id = R.string.permissions__camera_permission),
            rationaleText = stringResource(id = R.string.permissions__camera_permission_description),
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
    router: SettingsRouter,
    onAddClick: () -> Unit,
    padding: PaddingValues,
) {
    val activity = LocalContext.current as Activity
    val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    LazyColumn(modifier = Modifier.padding(padding)) {
        item { SettingsHeader(TwLocale.strings.browserExtPairedDevices) }

        items(uiState.pairedBrowsers, key = { it.id }) {
            SimpleEntry(title = it.name,
                iconVisibleWhenNotSet = true,
                subtitle = it.formatPairedAt(),
                click = { router.navigate(SettingsDirections.BrowserDetails(extensionId = it.id)) })
        }

        item {
            Button(
                onClick = { onAddClick() }, shape = ButtonShape(), modifier = Modifier.padding(start = 72.dp, top = 6.dp, bottom = 2.dp)
            ) {
                Text(text = stringResource(id = R.string.browser__add_new).uppercase(), color = ButtonTextColor())
            }
        }

        item { SettingsHeader(TwLocale.strings.browserExtDeviceName) }

        // TODO
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
                    style = MaterialTheme.typography.body2.copy(fontSize = 14.sp, color = MaterialTheme.colors.primary),
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
                    Text(text = "Enable Notifications".uppercase(), color = ButtonTextColor())
                }
            }
        }
    }

    if (uiState.showEditDeviceDialog) {
        InputDialog(
            prefill = uiState.mobileDevice?.name.orEmpty(),
            hint = stringResource(id = R.string.browser__device_name),
            allowEmpty = false,
            maxLength = 100,
            onDismiss = { viewModel.onEditDeviceDialogDismiss() },
            onPositive = { viewModel.onEditDeviceDialogDismiss(it) },
        )
    }
}

@Composable
internal fun EmptyScreen(
    router: SettingsRouter,
    padding: PaddingValues,
    onAddClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        val (content, pair) = createRefs()

        Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .constrainAs(content) {
                top.linkTo(parent.top)
                bottom.linkTo(pair.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .padding(vertical = 16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.browser_extension_start_image),
                contentDescription = null,
                modifier = Modifier
                    .height(130.dp)
                    .offset(y = (-16).dp)
            )

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
                    .clickable { uriHandler.openUri(TwLocale.links.browserExt) },
            )

            Text(
                text = stringResource(id = R.string.browser__info_description_first) + "\n\n" + stringResource(id = R.string.browser__info_description_second),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.browser__more_info) + " ")
                    withStyle(style = SpanStyle(MaterialTheme.colors.primary)) {
                        append(stringResource(id = R.string.browser__more_info_link_title))
                    }
                }, style = MaterialTheme.typography.body2, modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(CenterHorizontally)
                    .clickable {
                        activity?.openBrowserApp(url = "https://2fas.com/be")
                    }, textAlign = TextAlign.Center
            )
        }


        Button(onClick = { onAddClick() }, shape = ButtonShape(), modifier = Modifier
            .height(48.dp)
            .constrainAs(pair) {
                bottom.linkTo(parent.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text(text = stringResource(id = R.string.browser__pair_with_web_browser).uppercase(), color = ButtonTextColor())
        }
    }
}