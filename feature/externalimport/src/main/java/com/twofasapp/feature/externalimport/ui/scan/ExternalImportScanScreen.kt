package com.twofasapp.feature.externalimport.ui.scan

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.common.ktx.encodeBase64
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.toastShort
import com.twofasapp.core.design.theme.RoundedShape16
import com.twofasapp.core.design.theme.RoundedShape24
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.qrscan.QrScan
import com.twofasapp.feature.qrscan.QrScanFinder
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
internal fun ExternalImportScanScreen(
    importType: ImportType,
    initialContent: String?,
    fromGallery: Boolean,
    viewModel: ExternalImportScanViewModel = koinViewModel { parametersOf(initialContent, fromGallery) },
    navigator: Navigator = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val strings = MdtLocale.strings
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { viewModel.onImagePicked(it.toString()) } },
    )

    LaunchedEffect(uiState.importContent) {
        uiState.importContent?.let { content ->
            viewModel.consumeImportContent()
            navigator.open(
                Screen.ExternalImportResult(
                    importType = importType.name,
                    importFileContent = content.encodeBase64(),
                ),
            )
        }
    }

    LaunchedEffect(uiState.scanError) {
        uiState.scanError?.let { error ->
            context.toastShort(
                when (error) {
                    ScanError.AlreadyScanned -> strings.externalImportBatchAlreadyScanned
                    ScanError.DifferentExport -> strings.externalImportBatchDifferentExport
                    ScanError.ReadError -> strings.externalImportBatchReadError
                },
            )
            viewModel.consumeScanError()
        }
    }

    BackHandler(enabled = uiState.batch != null && uiState.scanning) {
        viewModel.stopScanning()
    }

    Content(
        uiState = uiState,
        onScanned = viewModel::onScanned,
        onScanNext = {
            if (uiState.fromGallery) {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                viewModel.startScanning()
            }
        },
        onImport = viewModel::importBatch,
    )
}

@Composable
private fun Content(
    uiState: ExternalImportScanUiState,
    onScanned: (String) -> Unit = {},
    onScanNext: () -> Unit = {},
    onImport: () -> Unit = {},
) {
    val strings = MdtLocale.strings
    val batch = uiState.batch

    Scaffold(
        topBar = { TopAppBar(title = strings.scanQr) },
    ) { padding ->
        if (batch == null || uiState.scanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.scanning) {
                    QrScan(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedShape24)
                            .background(Color.Black),
                        onScanned = onScanned,
                    )

                    QrScanFinder()
                }
            }
        } else {
            BatchContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                batch = batch,
                fromGallery = uiState.fromGallery,
                onScanNext = onScanNext,
                onImport = onImport,
            )
        }
    }
}

@Composable
private fun BatchContent(
    modifier: Modifier = Modifier,
    batch: BatchState,
    fromGallery: Boolean,
    onScanNext: () -> Unit,
    onImport: () -> Unit,
) {
    val strings = MdtLocale.strings
    val progress by animateFloatAsState(targetValue = batch.scanned.size.toFloat() / batch.size, label = "progress")

    Column(modifier = modifier) {
        Text(
            text = strings.externalImportBatchTitle,
            style = MdtTheme.typo.xl.medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = strings.externalImportBatchMsg.format(batch.size),
            style = MdtTheme.typo.base.normal,
            color = MdtTheme.color.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = strings.externalImportBatchProgress.format(batch.scanned.size, batch.size),
            style = MdtTheme.typo.sm.medium,
            color = MdtTheme.color.accentGreen,
        )

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MdtTheme.color.accentGreen,
            trackColor = MdtTheme.color.surfaceContainerHighest,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(batch.size) { index ->
                BatchItem(
                    index = index,
                    size = batch.size,
                    scanned = batch.scanned[index],
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (batch.isComplete) {
            Button(
                text = strings.externalImportBatchImportCta,
                onClick = onImport,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Button(
                text = strings.externalImportBatchImportCta,
                onClick = onImport,
                style = ButtonStyle.Text,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            Button(
                text = if (fromGallery) strings.externalImportBatchPickCta else strings.externalImportBatchScanCta,
                onClick = onScanNext,
                leadingIcon = MdtIcons.Qr,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun BatchItem(
    index: Int,
    size: Int,
    scanned: ScannedCode?,
) {
    val strings = MdtLocale.strings
    val isScanned = scanned != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedShape16)
            .background(if (isScanned) MdtTheme.color.accentGreen.copy(alpha = 0.12f) else MdtTheme.color.surfaceContainer)
            .border(
                width = 1.dp,
                color = if (isScanned) MdtTheme.color.accentGreen else MdtTheme.color.outlineVariant,
                shape = RoundedShape16,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .then(
                    if (isScanned) {
                        Modifier.background(MdtTheme.color.accentGreen)
                    } else {
                        Modifier.border(width = 2.dp, color = MdtTheme.color.outline, shape = CircleShape)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isScanned) {
                Icon(
                    painter = MdtIcons.Check,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            } else {
                Text(
                    text = (index + 1).toString(),
                    style = MdtTheme.typo.base.medium,
                    color = MdtTheme.color.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.size(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = strings.externalImportBatchQrCode.format(index + 1, size),
                style = MdtTheme.typo.base.medium,
                color = MdtTheme.color.onSurface,
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = if (scanned != null) {
                    strings.externalImportBatchScanned.format(scanned.servicesCount)
                } else {
                    strings.externalImportBatchNotScanned
                },
                style = MdtTheme.typo.sm.normal,
                color = if (isScanned) MdtTheme.color.accentGreen else MdtTheme.color.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            uiState = ExternalImportScanUiState(),
        )
    }
}

@Preview
@Composable
private fun PreviewBatch() {
    PreviewTheme {
        Content(
            uiState = ExternalImportScanUiState(
                scanning = false,
                batch = BatchState(
                    size = 3,
                    id = 0,
                    scanned = mapOf(0 to ScannedCode(content = "", servicesCount = 10)),
                ),
            ),
        )
    }
}