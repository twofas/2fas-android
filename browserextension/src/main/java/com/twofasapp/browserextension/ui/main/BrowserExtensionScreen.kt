package com.twofasapp.browserextension.ui.main

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.twofasapp.resources.R
import com.twofasapp.design.compose.*
import com.twofasapp.design.compose.dialogs.InputDialog
import com.twofasapp.design.compose.dialogs.RationaleDialog
import com.twofasapp.extensions.openBrowserApp
import com.twofasapp.navigation.SettingsDirections
import com.twofasapp.navigation.SettingsRouter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
internal fun BrowserExtensionScreen(
    viewModel: BrowserExtensionViewModel = get(),
    router: SettingsRouter = get(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { Toolbar(title = stringResource(id = R.string.browser__browser_extension)) { router.navigate(SettingsDirections.GoBack) } },
    ) { padding ->
        if (uiState.isLoading) return@Scaffold

        if (uiState.pairedBrowsers.isEmpty()) {
            EmptyScreen(viewModel, padding)
        } else {
            ContentScreen(
                viewModel = viewModel,
                uiState = uiState,
                router = router,
                padding = padding
            )
        }
    }

    uiState.getMostRecentEvent()?.let {
        when (it) {
            is BrowserExtensionUiState.Event.ShowSnackbarError -> {
                scope.launch {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it.message)
                    viewModel.eventHandled(it.id)
                }
            }
        }
    }
}

@Composable
internal fun ContentScreen(
    viewModel: BrowserExtensionViewModel,
    uiState: BrowserExtensionUiState,
    router: SettingsRouter,
    padding: PaddingValues,
) {
    LazyColumn(modifier = Modifier.padding(padding)) {
        item {
            HeaderEntry(text = stringResource(id = R.string.browser__paired_devices_browser_title))
        }

        items(uiState.pairedBrowsers, key = { it.id }) {
            SimpleEntry(
                title = it.name,
                iconVisibleWhenNotSet = true,
                subtitle = it.formatPairedAt(),
                click = { router.navigate(SettingsDirections.BrowserDetails(extensionId = it.id)) }
            )
        }

        item {
            Button(
                onClick = { viewModel.onPairBrowserClick() },
                shape = ButtonShape(),
                modifier = Modifier.padding(start = 72.dp, top = 6.dp, bottom = 2.dp)
            ) {
                Text(text = stringResource(id = R.string.browser__add_new).uppercase(), color = ButtonTextColor())
            }
        }

        item {
            HeaderEntry(text = stringResource(id = R.string.browser__this_device_name))
        }

        item {
            SimpleEntry(
                title = uiState.mobileDevice?.name.orEmpty(),
                subtitle = stringResource(id = R.string.browser__this_device_footer),
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
internal fun EmptyScreen(
    viewModel: BrowserExtensionViewModel,
    padding: PaddingValues,
) {
    val activity = (LocalContext.current as? Activity)

    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .padding(padding)) {
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
                painter = painterResource(id = R.drawable.browser_extension_start_image),
                contentDescription = null,
                modifier = Modifier
                    .height(130.dp)
                    .offset(y = (-16).dp)
            )

            Text(
                text = stringResource(id = R.string.browser__info_title),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.browser__info_description_first) + "\n\n" + stringResource(id = R.string.browser__info_description_second),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(text = buildAnnotatedString {
                append(stringResource(id = R.string.browser__more_info) + " ")
                withStyle(style = SpanStyle(MaterialTheme.colors.primary)) {
                    append(stringResource(id = R.string.browser__more_info_link_title))
                }
            }, style = MaterialTheme.typography.body2, modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(CenterHorizontally)
                .clickable {
                    activity?.openBrowserApp(url = "https://2fas.com/be")
                }, textAlign = TextAlign.Center)
        }

        Button(onClick = { viewModel.onPairBrowserClick() },
            shape = ButtonShape(),
            modifier = Modifier
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