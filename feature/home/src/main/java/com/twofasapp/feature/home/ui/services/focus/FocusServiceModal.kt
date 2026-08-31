package com.twofasapp.feature.home.ui.services.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.twofasapp.android.viewmodel.ProvidesViewModelStoreOwner
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.DsServiceModal
import com.twofasapp.core.design.feature.items.ServiceAuthType
import com.twofasapp.core.design.feature.items.ServiceImageType
import com.twofasapp.core.design.feature.items.ServiceState
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.foundation.modal.Modal
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

object FocusServiceModalNavArg {
    val ServiceId = navArgument("id") { type = NavType.LongType }
}

@Composable
fun FocusServiceModal(
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
            )
        }
    }
}

@Composable
private fun ModalContent(
    serviceId: Long,
    onEditService: (Long) -> Unit = {},
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
    onIncrementCounterClick: () -> Unit = {},
    onRevealClick: () -> Unit = {},
) {
    Column {
        DsServiceModal(
            state = serviceState,
            showNextCode = showNextCode,
            hideCodes = hideCodes,
            containerColor = MdtTheme.color.surface,
            onIncrementCounterClick = onIncrementCounterClick,
            onRevealClick = onRevealClick,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MdtTheme.color.surface)
                .padding(vertical = 16.dp),
        ) {
            OptionEntry(
                title = MdtLocale.strings.editService,
                icon = MdtIcons.Edit,
                onClick = onEditClick,
            )
            OptionEntry(
                title = MdtLocale.strings.copyToken,
                icon = MdtIcons.Copy,
                onClick = onCopyClick,
            )
        }
    }
}

@Preview
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