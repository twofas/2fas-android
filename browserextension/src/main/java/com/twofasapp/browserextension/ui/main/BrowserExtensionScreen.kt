package com.twofasapp.browserextension.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.dialogs.InputDialog
import com.twofasapp.design.compose.dialogs.RationaleDialog
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.screen.CommonContent
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
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

    viewModel.onPairClick = { openPairingScan() }
    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.browserExtTitle) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (uiState.isLoading) return@Scaffold

        if (uiState.pairedBrowsers.isEmpty()) {
            EmptyScreen(
                onPairBrowserClick = openPairingScan,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            )
        } else {
            ContentScreen(
                onBrowserClick = openBrowserDetails,
                onPairBrowserClick = openPairingScan,
                viewModel = viewModel,
                uiState = uiState,
                padding = padding
            )
        }
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

@Composable
private fun ContentScreen(
    onBrowserClick: (String) -> Unit,
    onPairBrowserClick: () -> Unit,
    viewModel: BrowserExtensionViewModel,
    uiState: BrowserExtensionUiState,
    padding: PaddingValues,
) {
    LazyColumn(modifier = Modifier.padding(padding)) {
        item { SettingsHeader(TwLocale.strings.browserExtPairedDevices) }

        items(uiState.pairedBrowsers, key = { it.id }) {
            SettingsLink(it.name, onClick = { onBrowserClick(it.id) })
            //                subtitle = it.formatPairedAt(),
        }

        item {
            TwButton(
                text = "+ ${TwLocale.strings.browserExtAddNew}",
                onClick = onPairBrowserClick,
                height = TwTheme.dimen.buttonHeightSmall,
                modifier = Modifier.padding(start = 72.dp, top = 6.dp, bottom = 2.dp)
            )
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
    }

    if (uiState.showRationaleDialog) {
        RationaleDialog(
            title = stringResource(id = R.string.permissions__camera_permission),
            text = stringResource(id = R.string.permissions__camera_permission_description),
            onDismiss = { viewModel.onRationaleDialogDismiss() }
        )
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
private fun EmptyScreen(
    onPairBrowserClick: () -> Unit,
    modifier: Modifier,
) {
    val uriHandler = LocalUriHandler.current

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
                    .clickable { uriHandler.openUri(TwLocale.links.browserExt) },
            )
        },
        modifier = modifier,
    )
}