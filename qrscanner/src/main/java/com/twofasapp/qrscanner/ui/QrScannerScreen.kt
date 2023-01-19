package com.twofasapp.qrscanner.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.twofasapp.design.compose.dialogs.SimpleDialog
import com.twofasapp.resources.R
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun QrScannerScreen(
    viewModel: QrScannerViewModel = get(),
    isGalleryEnabled: Boolean = false,
    startWithGallery: Boolean = false,
) {

    val showReadError = viewModel.showPhotoReadError.collectAsState().value
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result -> result.data?.data?.let { viewModel.onPhotoPicked(it) } }
    val startWithGallerySaveable = rememberSaveable { mutableStateOf(startWithGallery) }

    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)



    Scaffold(
        floatingActionButton = {
            if (isGalleryEnabled) {
                FloatingActionButton(
                    onClick = { galleryLauncher.launch(galleryIntent) },
                    backgroundColor = Color(0xFF4C4C4C),
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_photo_gallery), "", tint = Color.White)
                }
            }
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {
            QrScannerPreview(onScanned = { viewModel.onScanned(it) })

            Text(
                text = stringResource(id = R.string.tokens__scan_qr_code_title),
                style = MaterialTheme.typography.h5.copy(fontSize = 20.sp), color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .background(color = Color(0xBF000000))
                    .padding(8.dp)

            )

            Image(
                painter = painterResource(id = R.drawable.scanner_finder),
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.Center)
            )
        }

        if (showReadError) {
            SimpleDialog(
                title = stringResource(id = R.string.tokens__qr_read_image_failed),
                text = stringResource(id = R.string.tokens__qr_read_image_try_again),
                positiveText = stringResource(id = R.string.tokens__try_again),
                onDismiss = { viewModel.showPhotoReadError.update { false } },
                onPositive = {
                    viewModel.showPhotoReadError.update { false }
                    galleryLauncher.launch(galleryIntent)
                },
            )
        }

        if (startWithGallerySaveable.value) {
            galleryLauncher.launch(galleryIntent)
            startWithGallerySaveable.value = false
        }
    }
}

@Composable
internal fun QrScannerPreview(
    onScanned: (String) -> Unit
) {
    val scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = Modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val barcodeScanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                    ).build()
            )
            val cameraExecutor: ExecutorService by lazy {
                Executors.newSingleThreadExecutor()
            }

            val previewUseCase by lazy {
                Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            }
            val imageAnalysisUseCase by lazy {
                ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(barcodeScanner, imageProxy) { text ->
                            onScanned.invoke(text)
                        }
                    }
                }
            }

            coroutineScope.launch {
                val cameraProvider = context.getCameraProvider()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        previewUseCase,
                        imageAnalysisUseCase,
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, "Camera could not be launched. Try again.", Toast.LENGTH_LONG).show()
                }
            }

            previewView
        }
    )
}

@SuppressLint("UnsafeOptInUsageError")
private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onScanned: (String) -> Unit,
) {
    imageProxy.image?.let { image ->
        val inputImage = InputImage.fromMediaImage(
            image,
            imageProxy.imageInfo.rotationDegrees
        )

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodeList ->
                val barcode = barcodeList.getOrNull(0)
                barcode?.rawValue?.let { text -> onScanned.invoke(text) }
            }
            .addOnCompleteListener {
                imageProxy.image?.close()
                imageProxy.close()
            }
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            executor
        )
    }
}

val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)