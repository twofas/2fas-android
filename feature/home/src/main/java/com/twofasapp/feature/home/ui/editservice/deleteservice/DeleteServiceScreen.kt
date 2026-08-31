package com.twofasapp.feature.home.ui.editservice.deleteservice

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.TextButton
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.LocalBackDispatcher
import com.twofasapp.feature.home.ui.editservice.EditServiceUiEvent
import com.twofasapp.feature.home.ui.editservice.EditServiceViewModel
import com.twofasapp.locale.R
import kotlinx.coroutines.launch

@Composable
internal fun DeleteServiceScreen(
    viewModel: EditServiceViewModel,
) {
    val service = viewModel.uiState.collectAsState().value.service
    val backDispatcher = LocalBackDispatcher
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.events.collect {
                when (it) {
                    EditServiceUiEvent.Finish -> scope.launch { backDispatcher.onBackPressed() }
                }
            }
        }
    }

    Content(
        service = service,
        onDelete = { viewModel.delete() },
        onCancel = { backDispatcher.onBackPressed() },
    )
}

@Composable
private fun Content(
    service: Service,
    onDelete: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    Scaffold(
        topBar = { TopAppBar(title = "") },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(id = com.twofasapp.core.design.R.drawable.illustration_delete_confirm),
                    contentDescription = null,
                    modifier = Modifier.height(150.dp),
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = stringResource(id = R.string.delete_service_title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp),
                    color = MdtTheme.color.onSurface,
                )

                Text(
                    text = service.name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                    color = MdtTheme.color.onSurface,
                )

                Text(
                    text = stringResource(id = R.string.delete_service_msg, service.name),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MdtTheme.color.onSurface,
                )
            }

            Button(
                text = stringResource(id = R.string.delete_service_cta),
                onClick = { onDelete() },
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                text = stringResource(id = R.string.commons__cancel),
                onClick = { onCancel() },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            service = Service.Preview,
        )
    }
}