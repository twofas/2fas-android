package com.twofasapp.feature.home.ui.services.focus

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.viewmodel.ProvidesViewModelStoreOwner
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.ServiceAuthType
import com.twofasapp.core.design.feature.items.ServiceCard
import com.twofasapp.core.design.feature.items.ServiceImageType
import com.twofasapp.core.design.feature.items.ServiceState
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.modal.Modal
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ServiceModal(
    serviceId: Long,
    onDismissRequest: () -> Unit,
    openService: (Long) -> Unit,
) {
    Modal(
        onDismissRequest = onDismissRequest,
        animateContentSize = true,
    ) { dismiss ->
        ProvidesViewModelStoreOwner {
            ModalContent(
                serviceId = serviceId,
                onEditService = { id -> dismiss { openService(id) } },
                onServiceDeleted = { dismiss {} },
            )
        }
    }
}

@Composable
private fun ModalContent(
    serviceId: Long,
    onEditService: (Long) -> Unit = {},
    onServiceDeleted: () -> Unit = {},
    viewModel: FocusServiceViewModel = koinViewModel { parametersOf(serviceId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.currentActivity
    val serviceState = uiState.service?.asState() ?: ServiceState(
        name = " ",
        info = " ",
        code = "      ",
        nextCode = "",
        timer = 30,
        hotpCounter = null,
        hotpCounterEnabled = false,
        progress = 1f,
        imageType = ServiceImageType.Icon,
        authType = ServiceAuthType.Totp,
        iconLight = "",
        iconDark = "",
        labelText = null,
        labelColor = Color.Unspecified,
        badgeColor = Color.Unspecified,
        revealed = true,
    )

    Content(
        serviceState = serviceState,
        showNextCode = uiState.showNextCode,
        hideCodes = uiState.hideCodes,
        onEditClick = { uiState.service?.id?.let(onEditService) },
        onCopyClick = { serviceState.copyToClipboard(activity, uiState.showNextCode) },
        onDeleteClick = {
            viewModel.delete()
            onServiceDeleted()
        },
        onIncrementCounterClick = { viewModel.incrementCounter() },
        onRevealClick = { viewModel.reveal() },
    )
}

@Composable
private fun Content(
    serviceState: ServiceState,
    showNextCode: Boolean,
    hideCodes: Boolean,
    onEditClick: () -> Unit = {},
    onCopyClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onIncrementCounterClick: () -> Unit = {},
    onRevealClick: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column {
        Space(16.dp)

        ServiceCard(
            state = serviceState,
            showNextCode = showNextCode,
            hideCodes = hideCodes,
            containerColor = MdtTheme.color.surfaceContainerHigh,
            onIncrementCounterClick = onIncrementCounterClick,
            onRevealClick = onRevealClick,
        )

        Space(16.dp)

        OptionEntry(
            title = MdtLocale.strings.copyToken,
            icon = MdtIcons.Copy,
            onClick = onCopyClick,
        )

        OptionEntry(
            title = MdtLocale.strings.editService,
            icon = MdtIcons.Edit,
            onClick = onEditClick,
        )

        OptionEntry(
            title = MdtLocale.strings.commonDelete,
            icon = MdtIcons.Delete,
            onClick = { showDeleteDialog = true },
        )

        Space(24.dp)
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = MdtLocale.strings.commonDelete,
            body = MdtLocale.strings.servicesDelete,
            icon = MdtIcons.Delete,
            onPositive = { onDeleteClick() },
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            serviceState = ServiceState.Empty.copy(name = "Google", info = "john@gmail.com", code = "123456"),
            showNextCode = false,
            hideCodes = false,
        )
    }
}