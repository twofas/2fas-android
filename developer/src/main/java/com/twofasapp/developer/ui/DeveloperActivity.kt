package com.twofasapp.developer.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.design.compose.HeaderEntry
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.SwitchEntry
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.extensions.copyToClipboard
import com.twofasapp.extensions.restartApp
import com.twofasapp.resources.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DeveloperActivity : BaseComponentActivity() {

    private val viewModel: DeveloperViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainAppTheme {
                Scaffold(
                    topBar = {
                        TwTopAppBar(titleText = "Developer Options", actions = {
                            TextButton(onClick = { restartApp() }) {
                                Text(text = "Restart")
                            }
                        })
                    }
                ) { padding ->
                    ItemsList(Modifier.padding(padding))
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ItemsList(modifier: Modifier) {
        val uiState = viewModel.uiState.collectAsState().value

        LazyColumn(modifier) {
            item { HeaderEntry(text = "Feature toggles") }
            items(uiState.featureToggles.toList(), key = { it.first.name }) {
                SwitchEntry(
                    title = it.first.name,
                    icon = painterResource(id = R.drawable.ic_developer_toggle),
                    isChecked = it.second,
                    switch = { isChecked -> viewModel.onFeatureToggleChanged(it.first, isChecked) }
                )
            }

            if (uiState.fcmToken.isNotBlank()) {
                item { HeaderEntry(text = "Config") }
                item {
                    SimpleEntry(
                        title = "FCM Token",
                        subtitle = uiState.fcmToken,
                        icon = painterResource(id = R.drawable.ic_push),
                        click = { copyToClipboard(uiState.fcmToken) },
                        modifier = Modifier.animateItemPlacement(),
                    )
                }

            }

            if (uiState.lastScannedQr.text.isNotBlank()) {
                item { HeaderEntry(text = "QR Scanner") }
                item {
                    SimpleEntry(
                        title = "Last scanned QR",
                        subtitle = uiState.lastScannedQr.text,
                        icon = painterResource(id = R.drawable.ic_add_service_qr),
                        click = { copyToClipboard(uiState.lastScannedQr.text) },
                        modifier = Modifier.animateItemPlacement(),
                    )
                }
            }

            if (uiState.lastPushes.isNotEmpty()) {
                item { HeaderEntry(text = "Last 20 pushes") }
                items(uiState.lastPushes, key = { it.timestamp }) {

                    val subtitle = StringBuilder()
                        .apply {
                            it.data.forEach { entry -> appendLine("${entry.key}=${entry.value}") }
                            if (it.notificationTitle.isNullOrBlank().not()) {
                                appendLine("notification.title=${it.notificationTitle}")
                            }
                            if (it.notificationBody.isNullOrBlank().not()) {
                                appendLine("notification.body=${it.notificationBody}")
                            }
                        }.toString()

                    SimpleEntry(
                        title = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        subtitle = subtitle,
                        click = { copyToClipboard(subtitle) },
                        modifier = Modifier.animateItemPlacement(),
                    )
                }
            }

        }
    }
}
