package com.twofasapp.browserextension.ui.browser

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.dialogs.ConfirmDialog
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.resources.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun BrowserDetailsScreen(
    onFinish: () -> Unit,
    extensionId: String,
    viewModel: BrowserDetailsViewModel = koinViewModel(),
) {
    viewModel.init(extensionId)
    val uiState = viewModel.uiState.collectAsState().value
    val scope = rememberCoroutineScope()
    viewModel.onFinish = onFinish

    Scaffold(
//        scaffoldState = scaffoldState,
        topBar = {
            TwTopAppBar(titleText = stringResource(id = R.string.browser__browser_extension))
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                SimpleEntry(
                    title = "Name",
                    subtitle = uiState.browserName,
                )
            }

            item {
                SimpleEntry(
                    title = "Date paired",
                    subtitle = uiState.browserPairedAt,
                )
            }
            item { Divider(color = TwTheme.color.divider, modifier = Modifier.padding(vertical = 8.dp)) }
            item {
                OutlinedButton(
                    onClick = { viewModel.showConfirmForget() },
                    shape = CircleShape,
                    modifier = Modifier.padding(start = 72.dp, top = 6.dp, bottom = 2.dp),
                    border = BorderStroke(1.dp, TwTheme.color.primary)
                ) {
                    Text(text = "Forget this web browser")
                }
            }
        }

        if (uiState.showConfirmDeleteDialog) {
            ConfirmDialog(
                title = "Forget browser?",
                text = "This removes connection between your device and browser.",
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
//                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
//                    scaffoldState.snackbarHostState.showSnackbar(it.message)
                }
            }
        }
    }
}