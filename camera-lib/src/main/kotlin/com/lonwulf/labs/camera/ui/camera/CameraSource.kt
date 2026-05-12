/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lonwulf.labs.camera.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.common.images.Size
import com.lonwulf.labs.camera.domain.repository.FrameProcessor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Manages the camera and allows UI updates on top of it (e.g. overlaying extra Graphics). This
 * receives preview frames from the camera at a specified rate, sends those frames to detector as
 * fast as it is able to process.
 */
class CameraSource(private val graphicOverlay: GraphicOverlay) {

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var analysisExecutor: ExecutorService? = null

    internal var previewSize: Size? = null
        private set

    private val processorLock = Object()
    private var frameProcessor: FrameProcessor? = null

    private val context: Context = graphicOverlay.context

    @SuppressLint("UnsafeOptInUsageError")
    fun start(lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases(lifecycleOwner, surfaceProvider)
        }, ContextCompat.getMainExecutor(context))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases(lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
        val cameraProvider = cameraProvider ?: return

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview = Preview.Builder().build().apply {
            setSurfaceProvider(surfaceProvider)
        }

        analysisExecutor = Executors.newSingleThreadExecutor()
        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(analysisExecutor!!) { imageProxy ->
                    if (previewSize == null || previewSize?.width != imageProxy.width || previewSize?.height != imageProxy.height) {
                        previewSize = Size(imageProxy.width, imageProxy.height)
                    }
                    synchronized(processorLock) {
                        frameProcessor?.let { processor ->
                            processor.process(imageProxy, graphicOverlay)
                        } ?: imageProxy.close()
                    }
                }
            }

        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }

    @Synchronized
    fun stop() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        camera = null
        preview = null
        imageAnalysis = null
        analysisExecutor?.shutdown()
        analysisExecutor = null
    }

    fun release() {
        graphicOverlay.clear()
        synchronized(processorLock) {
            stop()
            frameProcessor?.stop()
        }
    }

    fun setFrameProcessor(processor: FrameProcessor) {
        graphicOverlay.clear()
        synchronized(processorLock) {
            frameProcessor?.stop()
            frameProcessor = processor
        }
    }

    fun updateFlashMode(flashMode: String) {
        val enable = flashMode == "torch" || flashMode == "on"
        camera?.cameraControl?.enableTorch(enable)
    }

    companion object {
        const val CAMERA_FACING_BACK = CameraSelector.LENS_FACING_BACK
        private const val TAG = "CameraSource"
    }
}
