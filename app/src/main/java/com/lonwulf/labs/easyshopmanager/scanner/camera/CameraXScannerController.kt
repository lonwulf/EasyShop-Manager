package com.lonwulf.labs.easyshopmanager.scanner.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Binds a CameraX `PreviewView` + `ImageAnalysis` pipeline and feeds frames into ML Kit
 * [FrameProcessor] implementations.
 *
 * The controller itself is not Compose-specific; Compose should call `start/stop` from
 * `DisposableEffect`.
 */
class CameraXScannerController(
    private val context: Context,
) {
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null
    private var isStarted: Boolean = false

    fun start(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        barcodeProcessor: FrameProcessor,
        barcodeOverlay: GraphicOverlay,
        objectProcessor: FrameProcessor,
        objectOverlay: GraphicOverlay,
        onAnalysisSize: (width: Int, height: Int) -> Unit,
    ) {
        if (isStarted) return
        isStarted = true

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val mainExecutor = ContextCompat.getMainExecutor(context)

        cameraProviderFuture.addListener(
            {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider

                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()

                var notified = false
                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    try {
                        if (!notified) {
                            notified = true
                            onAnalysisSize(imageProxy.width, imageProxy.height)
                        }

                        val nv21 = imageProxy.toNv21Bytes()
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        val metadata = FrameMetadata(
                            width = imageProxy.width,
                            height = imageProxy.height,
                            rotation = rotationDegrees
                        )

                        barcodeProcessor.process(nv21, metadata, barcodeOverlay)
                        objectProcessor.process(nv21, metadata, objectOverlay)
                    } finally {
                        imageProxy.close()
                    }
                }

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            },
            mainExecutor,
        )
    }

    fun stop() {
        if (!isStarted) return
        isStarted = false
        cameraProvider?.unbindAll()
    }

    fun release() {
        stop()
        cameraExecutor.shutdown()
    }
}

