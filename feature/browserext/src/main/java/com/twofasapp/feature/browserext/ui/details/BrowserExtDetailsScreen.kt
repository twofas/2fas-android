package com.twofasapp.feature.browserext.ui.details

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.core.design.feature.settings.SettingsLink
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BrowserExtDetailsScreen(
    viewModel: BrowserExtDetailsViewModel = koinViewModel(),
    openMain: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.finish) {
        if (uiState.finish) {
            openMain()
        }
    }

    ScreenContent(
        uiState = uiState,
        onForget = { viewModel.forgetBrowser() },
    )
}

@Composable
private fun ScreenContent(
    uiState: BrowserExtDetailsUiState,
    onForget: () -> Unit = {},
) {
    val strings = TwLocale.strings
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = strings.browserExtTitle) },
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                SettingsLink(
                    title = strings.browserDetailsName,
                    subtitle = uiState.browserName,
                )
            }

            item {
                SettingsLink(
                    title = strings.browserDetailsDate,
                    subtitle = TwLocale.formatDate(uiState.browserPairedAt),
                )
            }

            item {
                Button(
                    text = strings.browserDetailsForget,
                    style = ButtonStyle.Outlined,
                    onClick = { showConfirmDeleteDialog = true },
                    modifier = Modifier.padding(start = 72.dp, top = 16.dp),
                )
            }
        }
    }

    if (showConfirmDeleteDialog) {
        ConfirmDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            title = strings.browserDetailsForgetTitle,
            body = strings.browserDetailsForgetMsg,
            onPositive = onForget,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ScreenContent(
        uiState = BrowserExtDetailsUiState(browserName = "Test"),
    )
}