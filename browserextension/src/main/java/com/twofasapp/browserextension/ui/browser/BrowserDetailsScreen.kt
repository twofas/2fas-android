package com.twofasapp.browserextension.ui.browser

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twofasapp.resources.R
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.compose.dialogs.ConfirmDialog
import com.twofasapp.design.compose.dialogs.InputDialog
import com.twofasapp.design.theme.divider
import com.twofasapp.navigation.SettingsDirections
import com.twofasapp.navigation.SettingsRouter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
internal fun BrowserDetailsScreen(
    extensionId: String,
    viewModel: BrowserDetailsViewModel = get(),
    router: SettingsRouter = get(),
) {
    viewModel.init(extensionId)
    val uiState = viewModel.uiState.collectAsState().value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            Toolbar(title = stringResource(id = R.string.browser__browser_extension)) {
                router.navigate(SettingsDirections.GoBack)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                SimpleEntry(
                    title = stringResource(id = R.string.browser__name),
                    subtitle = uiState.browserName,
                )
            }

            item {
                SimpleEntry(
                    title = stringResource(id = R.string.browser__pairing_date),
                    subtitle = uiState.browserPairedAt,
                )
            }
            item { Divider(color = MaterialTheme.colors.divider, modifier = Modifier.padding(vertical = 8.dp)) }
            item {
                OutlinedButton(
                    onClick = { viewModel.showConfirmForget() },
                    shape = CircleShape,
                    modifier = Modifier.padding(start = 72.dp, top = 6.dp, bottom = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                ) {
                    Text(text = stringResource(id = R.string.browser__forget_this_browser))
                }
            }
        }

        if (uiState.showConfirmDeleteDialog) {
            ConfirmDialog(
                title = stringResource(id = R.string.browser__deleting_paired_device_title),
                text = stringResource(id = R.string.browser__deleting_paired_device_content),
                onDismiss = { viewModel.dismissConfirmForget() },
                onNegative = { viewModel.dismissConfirmForget() },
                onPositive = { viewModel.forgetBrowser() },
            )
        }
    }

    uiState.getMostRecentEvent()?.let {
        viewModel.eventHandled(it.id)

        when (it) {
            is BrowserDetailsUiState.Event.ShowSnackbarError -> {
                scope.launch {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it.message)
                }
            }
        }
    }
}