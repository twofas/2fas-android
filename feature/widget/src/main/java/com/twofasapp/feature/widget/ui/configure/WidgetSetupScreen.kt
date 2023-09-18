package com.twofasapp.feature.widget.ui.configure

import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwSwitch
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.service.DsServiceSimple
import com.twofasapp.designsystem.service.asState
import com.twofasapp.feature.widget.GlanceWidget
import com.twofasapp.locale.TwLocale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WidgetSetupScreen(
    viewModel: WidgetSetupViewModel = koinViewModel(),
    incoming: Intent,
    onSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.finishSuccess) {
        if (uiState.finishSuccess) {
            onSuccess()
        }
    }

    ScreenContent(
        uiState = uiState,
        onToggleService = { viewModel.toggleService(it) },
        onSave = {
            scope.launch {
                val glanceAppWidgetManager = GlanceAppWidgetManager(context)
                val glanceId = glanceAppWidgetManager.getGlanceIdBy(incoming)
                println("dupa: $glanceId")

                if(glanceId != null) {
                    updateAppWidgetState(context = context, glanceId = glanceId) {
                        it[intPreferencesKey("uid")] = 123
                    }

                    GlanceWidget().update(context, glanceId)
                }
            }

            viewModel.save()

        },
    )
}

@Composable
private fun ScreenContent(
    uiState: WidgetSetupUiState,
    onToggleService: (Long) -> Unit = {},
    onSave: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TwTopAppBar(
                titleText = TwLocale.strings.widgetSetupTitle,
                actions = {
                    TwTextButton(
                        text = TwLocale.strings.commonSave,
                        onClick = onSave,
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
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
    ScreenContent(WidgetSetupUiState())
}