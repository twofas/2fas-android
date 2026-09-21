package com.twofasapp.feature.browserext.ui.request

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.ServiceCardSimple
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPadding
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPaddingFirst
import com.twofasapp.core.design.feature.settings.OptionSwitch
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBarWithSearch
import com.twofasapp.core.design.ktx.LocalBackDispatcher
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import com.twofasapp.feature.browserext.notification.BrowserExtRequestReceiver
import com.twofasapp.locale.MdtLocale
import com.twofasapp.locale.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BrowserExtRequestScreen(
    viewModel: BrowserExtRequestViewModel = koinViewModel(),
    payload: BrowserExtRequestPayload,
) {
    val activity = LocalContext.currentActivity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init(payload)
    }

    ScreenContent(
        uiState = uiState,
        onSaveMyChoiceToggle = { viewModel.toggleSaveMyChoice() },
        onSearchChanged = { viewModel.updateSearchQuery(it) },
        onServiceClick = { service ->
            activity.lifecycleScope.launch {
                viewModel.assignDomain(service)

                // Clear notification
                NotificationManagerCompat
                    .from(activity)
                    .cancel(null, payload.requestId.hashCode())

                // Launch broadcast
                val intent = Intent(activity, BrowserExtRequestReceiver::class.java)
                    .apply {
                        action = BrowserExtRequestReceiver.ACTION
                        putExtra(BrowserExtRequestPayload.Key, payload.copy(serviceId = service.id))
                    }

                activity.finish()
                activity.sendBroadcast(intent)
            }
        },
    )
}

@Composable
private fun ScreenContent(
    uiState: BrowserExtRequestUiState,
    onSaveMyChoiceToggle: () -> Unit = {},
    onServiceClick: (Service) -> Unit = {},
    onSearchChanged: (String) -> Unit = {},
) {
    val strings = MdtLocale.strings
    val backDispatcher = LocalBackDispatcher

    Scaffold(
        topBar = {
            TopAppBarWithSearch(
                title = strings.browserRequestTitle,
                searchHint = stringResource(id = R.string.commons__search),
                onSearchValueChanged = {
                    onSearchChanged(it)
                },
            ) {
                backDispatcher.onBackPressed()
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .imePadding(),
        ) {
            item("Info") {
                Text(
                    text = strings.browserRequestInfo.format(uiState.browserName, uiState.domain),
                    style = MdtTheme.typo.sm.normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }

            item("Switch") {
                OptionSwitch(
                    title = strings.browserRequestSaveChoice,
                    checked = uiState.saveMyChoice,
                    onToggle = { onSaveMyChoiceToggle() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (uiState.suggestedServices.isNotEmpty()) {
                item("HeaderSuggested") {
                    OptionHeader(
                        text = strings.browserRequestSuggested,
                        contentPadding = OptionHeaderContentPaddingFirst,
                    )
                }

                items(items = uiState.suggestedServices, key = { it.id }) {
                    ServiceItem(
                        service = it,
                        onClick = onServiceClick,
                    )
                }
            }

            if (uiState.otherServices.isNotEmpty()) {
                item("HeaderOther") {
                    OptionHeader(
                        text = if (uiState.suggestedServices.isEmpty()) strings.browserRequestAll else strings.browserRequestOther,
                        contentPadding = if (uiState.suggestedServices.isEmpty()) OptionHeaderContentPaddingFirst else OptionHeaderContentPadding,
                    )
                }

                items(items = uiState.otherServices, key = { it.id }) {
                    ServiceItem(
                        service = it,
                        onClick = onServiceClick,
                    )
                }
            }

            if (uiState.suggestedServices.isEmpty() && uiState.otherServices.isEmpty()) {
                item("Empty") {
                    Text(
                        text = strings.browserRequestEmpty,
                        color = MdtTheme.color.onSurfaceVariant,
                        style = MdtTheme.typo.base.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceItem(
    service: Service,
    onClick: (Service) -> Unit,
) {
    ServiceCardSimple(
        state = service.asState(),
        onClick = { onClick(service) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        ScreenContent(
            uiState = BrowserExtRequestUiState(
                browserName = "{browser}",
                domain = "{domain}",
                suggestedServices = listOf(Service.Preview),
                otherServices = listOf(Service.Preview.copy(id = 1)),
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun Empty() {
    PreviewTheme {
        ScreenContent(
            uiState = BrowserExtRequestUiState(
                browserName = "{browser}",
                domain = "{domain}",
                suggestedServices = emptyList(),
                otherServices = emptyList(),
            ),
        )
    }
}