package com.twofasapp.services.ui.domainassignment

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.dialogs.ConfirmDialog
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.LocalBackDispatcher
import com.twofasapp.resources.R
import com.twofasapp.services.ui.ServiceViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DomainAssignmentScreen(
    viewModel: ServiceViewModel,
) {

    val service = viewModel.uiState.collectAsState().value.service
    val showConfirmDialog = remember { mutableStateOf(false) }
    val clickedDomainName = remember { mutableStateOf("") }
    val backDispatcher = LocalBackDispatcher

    Scaffold(
        topBar = { TwTopAppBar(titleText = stringResource(id = R.string.browser__browser_extension)) }
    ) { padding ->

        if (showConfirmDialog.value) {
            ConfirmDialog(
                title = stringResource(id = R.string.browser__deleting_extension_pairing_title),
                text = stringResource(id = R.string.browser__deleting_extension_pairing_content, clickedDomainName.value),
                onPositive = {
                    showConfirmDialog.value = false
                    viewModel.deleteDomainAssignment(clickedDomainName.value)
                },
                onNegative = { showConfirmDialog.value = false },
                onDismiss = { showConfirmDialog.value = false },
            )
        }

        if (service.assignedDomains.isEmpty()) {
            backDispatcher.onBackPressed()
        }

        LazyColumn(Modifier.padding(padding)) {
            item {
                Text(
                    text = stringResource(id = R.string.browser__paired_domains_list_title),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 72.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(color = TwTheme.color.onSurfaceSecondary)
                )
            }

            item { Divider(color = TwTheme.color.divider) }

            items(service.assignedDomains, key = { it }) {
                Column {
                    SimpleEntry(
                        modifier = Modifier.animateItemPlacement(),
                        title = it,
                        iconVisibleWhenNotSet = true,
                        iconEnd = painterResource(id = R.drawable.ic_remove_assigned_domain),
                        iconEndTint = TwTheme.color.primary,
                        iconEndClick = {
                            clickedDomainName.value = it
                            showConfirmDialog.value = true
                        }
                    )

                    Divider(color = TwTheme.color.divider)
                }
            }
        }
    }
}
