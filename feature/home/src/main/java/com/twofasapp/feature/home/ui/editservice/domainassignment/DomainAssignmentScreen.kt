package com.twofasapp.feature.home.ui.editservice.domainassignment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPaddingFirst
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.LocalBackDispatcher
import com.twofasapp.feature.home.ui.editservice.EditServiceViewModel
import com.twofasapp.locale.R

@Composable
internal fun DomainAssignmentScreen(
    viewModel: EditServiceViewModel,
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

@Composable
private fun Content(
    service: Service,
    onDeleteDomain: (String) -> Unit = {},
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var clickedDomainName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = stringResource(id = R.string.browser__browser_extension)) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .padding(top = padding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                OptionHeader(
                    text = stringResource(id = R.string.browser__paired_domains_list_title),
                    contentPadding = OptionHeaderContentPaddingFirst,
                )
            }

            items(items = service.assignedDomains, key = { it }) { domain ->
                OptionEntry(
                    modifier = Modifier.animateItem(),
                    title = domain,
                    icon = MdtIcons.Extension,
                    content = {
                        IconButton(
                            icon = MdtIcons.Delete,
                            iconTint = MdtTheme.color.primary,
                            onClick = {
                                clickedDomainName = domain
                                showConfirmDialog = true
                            },
                        )
                    },
                )
            }
        }

        if (showConfirmDialog) {
            ConfirmDialog(
                title = stringResource(id = R.string.browser__deleting_extension_pairing_title),
                body = stringResource(id = R.string.browser__deleting_extension_pairing_content, clickedDomainName),
                onPositive = {
                    showConfirmDialog = false
                    onDeleteDomain(clickedDomainName)
                },
                onNegative = { showConfirmDialog = false },
                onDismissRequest = { showConfirmDialog = false },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            service = Service.Preview.copy(assignedDomains = listOf("google.com", "github.com")),
        )
    }
}