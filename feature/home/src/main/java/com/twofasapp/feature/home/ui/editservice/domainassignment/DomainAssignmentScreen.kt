package com.twofasapp.feature.home.ui.editservice.domainassignment

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.LocalBackDispatcher
import com.twofasapp.locale.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DomainAssignmentScreen(
    viewModel: com.twofasapp.feature.home.ui.editservice.EditServiceViewModel,
) {
    val service = viewModel.uiState.collectAsState().value.service
    val backDispatcher = LocalBackDispatcher

    LaunchedEffect(service.assignedDomains.isEmpty()) {
        if (service.assignedDomains.isEmpty()) {
            backDispatcher.onBackPressed()
        }
    }

    Content(
        service = service,
        onDeleteDomain = { viewModel.deleteDomainAssignment(it) },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    service: Service,
    onDeleteDomain: (String) -> Unit = {},
) {
    val showConfirmDialog = remember { mutableStateOf(false) }
    val clickedDomainName = remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = stringResource(id = R.string.browser__browser_extension)) },
    ) { padding ->

        if (showConfirmDialog.value) {
            ConfirmDialog(
                title = stringResource(id = R.string.browser__deleting_extension_pairing_title),
                body = stringResource(id = R.string.browser__deleting_extension_pairing_content, clickedDomainName.value),
                onPositive = {
                    showConfirmDialog.value = false
                    onDeleteDomain(clickedDomainName.value)
                },
                onNegative = { showConfirmDialog.value = false },
                onDismissRequest = { showConfirmDialog.value = false },
            )
        }

        LazyColumn(Modifier.padding(padding)) {
            item {
                Text(
                    text = stringResource(id = R.string.browser__paired_domains_list_title),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 72.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MdtTheme.color.onSurfaceVariant),
                )
            }

            item { HorizontalDivider(color = MdtTheme.color.divider) }

            items(items = service.assignedDomains, key = { it }) {
                Column {
                    OptionEntry(
                        modifier = Modifier.animateItem(),
                        title = it,
                        content = {
                            IconButton(
                                icon = MdtIcons.Delete,
                                iconTint = MdtTheme.color.primary,
                                onClick = {
                                    clickedDomainName.value = it
                                    showConfirmDialog.value = true
                                },
                            )
                        },
                    )

                    HorizontalDivider(color = MdtTheme.color.divider)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            service = Service.Preview.copy(assignedDomains = listOf("google.com", "github.com")),
        )
    }
}