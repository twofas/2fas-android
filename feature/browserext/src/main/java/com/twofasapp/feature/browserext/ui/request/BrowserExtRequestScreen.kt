package com.twofasapp.feature.browserext.ui.request

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import com.twofasapp.feature.browserext.notification.BrowserExtRequestReceiver
import com.twofasapp.common.domain.Service
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwSwitch
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.service.DsServiceSimple
import com.twofasapp.designsystem.service.asState
import com.twofasapp.locale.TwLocale
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
        onServiceClick = { service ->
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

            activity.sendBroadcast(intent)
            activity.finish()
        }
    )
}

@Composable
private fun ScreenContent(
    uiState: BrowserExtRequestUiState,
    onSaveMyChoiceToggle: () -> Unit = {},
    onServiceClick: (Service) -> Unit = {},
) {
    val strings = TwLocale.strings

    Scaffold(
        topBar = { TwTopAppBar(titleText = strings.browserRequestTitle) },
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                Text(
                    text = strings.browserRequestInfo.format(uiState.browserName, uiState.domain),
                    style = TwTheme.typo.body3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { onSaveMyChoiceToggle() }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = strings.browserRequestSaveChoice,
                        color = TwTheme.color.onSurfacePrimary,
                        style = TwTheme.typo.body1,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TwSwitch(
                        checked = uiState.saveMyChoice,
                        onCheckedChange = { onSaveMyChoiceToggle() },
                    )
                }
            }

            if (uiState.suggestedServices.isNotEmpty()) {
                item {
                    SectionItem(title = strings.browserRequestSuggested)
                }

                items(items = uiState.suggestedServices, key = { it.id }) {
                    ServiceItem(
                        service = it,
                        onClick = onServiceClick
                    )
                }
            }

            if (uiState.otherServices.isNotEmpty()) {
                item {
                    SectionItem(title = if (uiState.suggestedServices.isEmpty()) strings.browserRequestAll else strings.browserRequestOther)
                }

                items(items = uiState.otherServices, key = { it.id }) {
                    ServiceItem(
                        service = it,
                        onClick = onServiceClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionItem(title: String) {
    Text(
        text = title.uppercase(),
        modifier = Modifier
            .fillMaxWidth()
            .background(TwTheme.color.surfaceVariant)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        color = TwTheme.color.onSurfaceSecondary,
        style = TwTheme.typo.body4,
    )
}

@Composable
private fun ServiceItem(
    service: Service,
    onClick: (Service) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        DsServiceSimple(
            state = service.asState(),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(service) }
                .padding(horizontal = 16.dp)
        )
        HorizontalDivider(color = TwTheme.color.divider)
    }
}

@Preview
@Composable
private fun Preview() {
    ScreenContent(
        uiState = BrowserExtRequestUiState(
            browserName = "{browser}",
            domain = "{domain}",
            suggestedServices = listOf(Service.Preview),
            otherServices = listOf(Service.Preview.copy(id = 1)),
        ),
    )
}
