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
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwCircularProgressIndicator
import com.twofasapp.designsystem.common.TwSwitch
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.service.DsServiceSimple
import com.twofasapp.designsystem.service.asState
import com.twofasapp.feature.widget.GlanceWidget
import com.twofasapp.locale.TwLocale
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
            TwTopAppBar(
                titleText = TwLocale.strings.widgetSettingsTitle,
                actions = {
                    TwTextButton(
                        text = TwLocale.strings.commonSave,
                        onClick = onSave,
                        enabled = uiState.loading.not() && uiState.services.isNotEmpty(),
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            if (uiState.loading) {
                item("Loader", "Loader") {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        TwCircularProgressIndicator()
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
                            text = TwLocale.strings.widgetSettingsEmpty,
                            style = TwTheme.typo.body1,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                return@LazyColumn
            }

            item("Info", "Info") {
                Text(
                    text = TwLocale.strings.widgetSelectMsg,
                    style = TwTheme.typo.body1.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(16.dp)
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
                    TwSwitch(
                        checked = uiState.selected.contains(service.id),
                        onCheckedChange = { onToggleService(service.id) }
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