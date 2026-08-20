package com.twofasapp.feature.widget.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.DsServiceSimple
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.foundation.button.TextButton
import com.twofasapp.core.design.foundation.checked.Switch
import com.twofasapp.core.design.foundation.progress.CircularProgressIndicator
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.feature.widget.GlanceWidget
import com.twofasapp.locale.MdtLocale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WidgetSettingsScreen(
    viewModel: WidgetSettingsViewModel = koinViewModel(),
    appWidgetId: Int,
    onSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.currentActivity
    val scope = rememberCoroutineScope()

    BackHandler {
        activity.finishAndRemoveTask()
    }

    LaunchedEffect(Unit) {
        viewModel.updateAppWidgetId(appWidgetId)
    }

    ScreenContent(
        uiState = uiState,
        onToggleService = { viewModel.toggleService(it) },
        onSave = {
            scope.launch {
                GlanceWidget().updateAll(activity)
                viewModel.save()
                onSuccess()
            }
        },
    )
}

@Composable
private fun ScreenContent(
    uiState: WidgetSettingsUiState,
    onToggleService: (Long) -> Unit = {},
    onSave: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = MdtLocale.strings.widgetSettingsTitle,
                actions = {
                    TextButton(
                        text = MdtLocale.strings.commonSave,
                        onClick = onSave,
                        enabled = uiState.loading.not() && uiState.services.isNotEmpty(),
                    )
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding),
        ) {
            if (uiState.loading) {
                item("Loader", "Loader") {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                return@LazyColumn
            }

            if (uiState.services.isEmpty()) {
                item("Empty", "Empty") {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = MdtLocale.strings.widgetSettingsEmpty,
                            style = MdtTheme.typo.base.normal,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                return@LazyColumn
            }

            item("Info", "Info") {
                Text(
                    text = MdtLocale.strings.widgetSelectMsg,
                    style = MdtTheme.typo.base.normal.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(16.dp),
                )
            }

            items(uiState.services, { it.id }, { "Service" }) { service ->
                DsServiceSimple(
                    state = service.asState().copy(revealed = true),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleService(service.id) }
                        .padding(start = 16.dp, end = 16.dp),
                ) {
                    Switch(
                        checked = uiState.selected.contains(service.id),
                        onCheckedChange = { onToggleService(service.id) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ScreenContent(WidgetSettingsUiState())
}

/**
 * This snippet lets you save custom data (example appWidgetId) after finishing configure activity.
 */

//            scope.launch {
//                val glanceAppWidgetManager = GlanceAppWidgetManager(context)
//                val glanceId = glanceAppWidgetManager.getGlanceIdBy(incoming)
//
//                if (glanceId != null) {
//                    updateAppWidgetState(context = context, glanceId = glanceId) {
//                        it[intPreferencesKey("appWidgetId")] = appWidgetId
//                    }
//
//                    GlanceWidget().update(context, glanceId)
//                }
//            }