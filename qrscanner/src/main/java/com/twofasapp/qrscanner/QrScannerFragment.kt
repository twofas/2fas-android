package com.twofasapp.qrscanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.twofasapp.base.BaseFragmentPresenter
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.qrscanner.databinding.FragmentQrScannerBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrScannerFragment : BaseFragmentPresenter<FragmentQrScannerBinding>(), QrScannerContract.View, AuthAware {

    companion object {
        fun newInstance() = QrScannerFragment()
    }

    private val presenter: QrScannerContract.Presenter by injectThis()

    private val barcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
            ).build()
    )
    private val cameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val previewUseCase by lazy {
        Preview.Builder().build().also {
            it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
        }
    }
    private val imageAnalysisUseCase by lazy {
        ImageAnalysis.Builder().build().also {
            it.setAnalyzer(cameraExecutor) { imageProxy -> processImageProxy(barcodeScanner, imageProxy) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return super.onCreateView(inflater, container, savedInstanceState, FragmentQrScannerBinding::inflate)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPresenter(presenter)
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    override fun onAuthenticated() {
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    previewUseCase,
                    imageAnalysisUseCase
                )

            } catch (exc: Exception) {
                Toast.makeText(requireContext(), "Camera could not be launched. Try again.", Toast.LENGTH_LONG).show()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(
                image,
                imageProxy.imageInfo.rotationDegrees
            )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodeList ->
                    val barcode = barcodeList.getOrNull(0)
                    barcode?.rawValue?.let { text ->
                        presenter.onScanned(text)
                    }
                }
                .addOnCompleteListener {
                    imageProxy.image?.close()
                    imageProxy.close()
                }
        }
    }
}
