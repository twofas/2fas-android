package com.twofasapp.browserextension.ui.request

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.browserextension.notification.BrowserExtensionRequestPayload
import com.twofasapp.browserextension.notification.BrowserExtensionRequestReceiver
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.theme.AppThemeLegacy
import com.twofasapp.design.theme.backgroundSecondary
import com.twofasapp.design.theme.textPrimary
import com.twofasapp.design.theme.textSecondary
import com.twofasapp.resources.R
import com.twofasapp.services.domain.model.Service
import com.twofasapp.services.view.ServiceCompact
import org.koin.androidx.compose.get

class BrowserExtensionRequestActivity : BaseComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val payload = intent.getParcelableExtra<BrowserExtensionRequestPayload>(BrowserExtensionRequestPayload.Key)!!

        setContent {
            AppThemeLegacy {
                Scaffold(
                    topBar = { Toolbar(title = stringResource(id = R.string.browser__request)) { onBackPressed() } }
                ) { padding ->
                    MainScreen(padding, payload)
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    internal fun MainScreen(
        padding: PaddingValues,
        payload: BrowserExtensionRequestPayload,
        viewModel: BrowserExtensionRequestViewModel = get()
    ) {
        viewModel.init(
            extensionId = payload.extensionId,
            domain = payload.domain,
        )

        val uiState = viewModel.uiState.collectAsState().value

        if (uiState.browserName.isBlank()) {
            return
        }

        if (uiState.otherServices.isEmpty() && uiState.suggestedServices.isEmpty()) {
            EmptyItem()
            return
        }

        LazyColumn(modifier = Modifier.padding(padding)) {
            item { HeaderItem(browserName = uiState.browserName, payload.domain, modifier = Modifier.animateItemPlacement()) }

            if (uiState.suggestedServices.isNotEmpty()) {
                item { SectionItem(title = stringResource(id = R.string.extension__services_suggested_header), modifier = Modifier.animateItemPlacement()) }
                items(uiState.suggestedServices, key = { it.id }) {

                    ServiceItem(
                        viewModel = viewModel,
                        service = it,
                        payload = payload,
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }

            if (uiState.otherServices.isNotEmpty()) {
                item {
                    SectionItem(
                        title = stringResource(
                            id = if (uiState.suggestedServices.isEmpty()) {
                                R.string.extension__services_all_header
                            } else {
                                R.string.extension__services_other_header
                            }
                        ),
                        modifier = Modifier.animateItemPlacement()
                    )
                }
                items(uiState.otherServices, key = { it.id }) {

                    ServiceItem(
                        viewModel = viewModel,
                        service = it,
                        payload = payload,
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }

    @Composable
    internal fun HeaderItem(browserName: String, domain: String?, modifier: Modifier) {
        Text(
            text = stringResource(id = R.string.browser__request_source_description).format(browserName, domain),
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 72.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.textPrimary)
        )
    }

    @Composable
    internal fun EmptyItem() {
        Text(
            text = stringResource(id = R.string.extension__error_no_services),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.textPrimary)
        )
    }

    @Composable
    internal fun SectionItem(title: String, modifier: Modifier) {
        Text(
            text = title.uppercase(),
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.backgroundSecondary)
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.textSecondary)
        )
    }

    @Composable
    internal fun ServiceItem(
        viewModel: BrowserExtensionRequestViewModel,
        service: Service,
        payload: BrowserExtensionRequestPayload,
        modifier: Modifier
    ) {
        val activity = (LocalContext.current as? Activity)

        ServiceCompact(
            service = service,
            showDivider = true,
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    Intent(activity, BrowserExtensionRequestReceiver::class.java)
                        .apply {
                            action = BrowserExtensionRequestReceiver.ACTION
                            putExtra(BrowserExtensionRequestPayload.Key, payload.copy(serviceId = service.id))
                        }
                        .let {
                            viewModel.assignDomain(service, payload.domain.orEmpty()) {
                                activity?.sendBroadcast(it)
                                finish()
                            }
                        }
                }
                .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
        )
    }
}
