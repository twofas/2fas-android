package com.twofasapp.feature.home.ui.services.add.scan

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.viewmodel.ProvidesViewModelStoreOwner
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.dialog.InfoDialog
import com.twofasapp.core.design.foundation.modal.Modal
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.ktx.settingsIntent
import com.twofasapp.core.design.theme.RoundedShape24
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.feature.home.ui.services.add.success.AddServiceSuccessContent
import com.twofasapp.feature.permissions.PermissionStatus
import com.twofasapp.feature.permissions.RequestPermission
import com.twofasapp.feature.permissions.rememberPermissionState
import com.twofasapp.feature.qrscan.QrScan
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddServiceScanModal(
    onDismissRequest: () -> Unit,
    openManual: () -> Unit = {},
    openGuides: () -> Unit = {},
    onAddedSuccessfully: (RecentlyAddedService) -> Unit = {},
) {
    Modal(
        onDismissRequest = onDismissRequest,
    ) { dismiss ->
        ProvidesViewModelStoreOwner {
            val viewModel: AddServiceScanViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.addedService) {
                uiState.addedService?.let(onAddedSuccessfully)
            }

            AnimatedContent(
                targetState = uiState.addedService,
                label = "AddServiceScan",
            ) { addedService ->
                if (addedService != null) {
                    AddServiceSuccessContent(
                        serviceId = addedService.serviceId,
                    )
                } else {
                    AddServiceScanContent(
                        uiState = uiState,
                        onManualClick = { dismiss { openManual() } },
                        onGuidesClick = { dismiss { openGuides() } },
                        onScanned = { viewModel.onScanned(it) },
                        onLoadFromGallery = { viewModel.onLoadFromGallery(it) },
                        onResetScanner = { viewModel.resetScanner() },
                        onServiceExistsConfirm = { viewModel.saveScannedService(uiState.scanned, uiState.source) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AddServiceScanContent(
    uiState: AddServiceScanUiState,
    onManualClick: () -> Unit = {},
    onGuidesClick: () -> Unit = {},
    onScanned: (String) -> Unit = {},
    onLoadFromGallery: (Uri) -> Unit = {},
    onResetScanner: () -> Unit = {},
    onServiceExistsConfirm: () -> Unit = {},
) {
    val context = LocalContext.current
    var cameraAskForPermission by remember { mutableStateOf(true) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { onLoadFromGallery(it) } },
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Space(16.dp)

        Text(
            text = MdtLocale.strings.addTitle,
            style = MdtTheme.typo.lg.medium,
            color = MdtTheme.color.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
        )

        Space(12.dp)

        Text(
            text = MdtLocale.strings.addDescription,
            color = MdtTheme.color.onSurfaceVariant,
            style = MdtTheme.typo.sm.normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Box(
            modifier = Modifier
                .padding(16.dp),
        ) {
            QrScan(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedShape24),
                onScanned = {
                    if (uiState.enabled) {
                        onScanned(it)
                    }
                },
            )

            if (cameraPermissionState.status is PermissionStatus.Denied) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Space(8.dp)

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = MdtLocale.strings.permissionCameraBody,
                        style = MdtTheme.typo.xs.normal,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                    )

                    Space(8.dp)

                    Button(
                        text = MdtLocale.strings.settingsSettings,
                        style = ButtonStyle.Text,
                        onClick = { context.startActivity(context.settingsIntent) },
                    )
                }
            }
        }

        Space(8.dp)

        Text(
            text = MdtLocale.strings.addOtherMethods,
            color = MdtTheme.color.onSurface,
            style = MdtTheme.typo.sm.medium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Space(8.dp)

        OptionEntry(
            title = MdtLocale.strings.addEnterManual,
            icon = MdtIcons.Keyboard,
            onClick = onManualClick,
        )

        OptionEntry(
            title = MdtLocale.strings.addFromGallery,
            icon = MdtIcons.Panorama,
            onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
        )

        OptionEntry(
            title = MdtLocale.strings.addWithGuide,
            icon = MdtIcons.Guide,
            onClick = onGuidesClick,
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (uiState.showInvalidQrDialog) {
        ConfirmDialog(
            onDismissRequest = onResetScanner,
            title = MdtLocale.strings.addScanInvalidQrTitle,
            body = MdtLocale.strings.addScanInvalidQrBody,
            icon = MdtIcons.Warning,
            positive = MdtLocale.strings.addScanInvalidQrCta,
            negative = null,
        )
    }

    if (uiState.showServiceExistsDialog) {
        ConfirmDialog(
            onDismissRequest = onResetScanner,
            title = MdtLocale.strings.addScanServiceExistsTitle,
            body = MdtLocale.strings.addScanServiceExistsBody,
            icon = MdtIcons.Warning,
            positive = MdtLocale.strings.addScanServiceExistsPositiveCta,
            negative = MdtLocale.strings.addScanServiceExistsNegativeCta,
            onPositive = onServiceExistsConfirm,
            onNegative = onResetScanner,
        )
    }

    if (uiState.showErrorDialog) {
        InfoDialog(
            onDismissRequest = onResetScanner,
            title = MdtLocale.strings.addScanErrorTitle,
            body = MdtLocale.strings.addScanErrorBody,
            icon = MdtIcons.ErrorCircle,
            positive = MdtLocale.strings.addScanErrorPositiveCta,
        )
    }

    if (uiState.showGalleryErrorDialog) {
        InfoDialog(
            onDismissRequest = onResetScanner,
            title = MdtLocale.strings.addGalleryErrorTitle,
            body = MdtLocale.strings.addGalleryErrorBody,
            icon = MdtIcons.ErrorCircle,
            positive = MdtLocale.strings.addGalleryErrorPositiveCta,
            onPositive = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
        )
    }

    if (cameraAskForPermission) {
        RequestPermission(
            permission = Manifest.permission.CAMERA,
            rationaleEnabled = false,
            onGranted = { cameraAskForPermission = false },
            onDismissRequest = { cameraAskForPermission = false },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        AddServiceScanContent(uiState = AddServiceScanUiState())
    }
}