package com.lonwulf.labs.easyshopmanager.scanner.camera

import android.Manifest
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay
import com.lonwulf.labs.easyshopmanager.scanner.util.Utils
import kotlin.math.abs

class CameraSource(
    private val context: Context,
    private val graphicOverlay: GraphicOverlay,
    private val cameraId: String
) {

    private val TAG = "CameraSource"

    private val cameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null

    private var rotationDegrees: Int = 0
    var previewSize: Size? = null
        private set

    private val processorLock = Any()
    private var frameProcessor: FrameProcessor? = null

    private val processingRunnable = FrameProcessingRunnable()
    private var processingThread: Thread? = null

    // Camera thread (REQUIRED for Camera2 public APIs)
    private val cameraThread = HandlerThread("Camera2Thread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)

    // ------------------------------------
    // Public API
    // ------------------------------------

    @RequiresPermission(Manifest.permission.CAMERA)
    fun start() {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)

        val sizePair = selectSizePair(characteristics)
            ?: throw IllegalStateException("No suitable preview size found")

        previewSize = sizePair.preview

        imageReader = ImageReader.newInstance(
            previewSize!!.width,
            previewSize!!.height,
            ImageFormat.YUV_420_888,
            2
        )

        imageReader!!.setOnImageAvailableListener(
            { reader ->
                val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                processingRunnable.setNextFrame(image)
            },
            cameraHandler
        )

        setRotation(characteristics)

        cameraManager.openCamera(cameraId, stateCallback, cameraHandler)

        processingThread = Thread(processingRunnable).apply {
            processingRunnable.setActive(true)
            start()
        }
    }

    fun stop() {
        processingRunnable.setActive(false)

        processingThread?.join()
        processingThread = null

        captureSession?.close()
        cameraDevice?.close()
        imageReader?.close()

        captureSession = null
        cameraDevice = null
        imageReader = null
    }

    fun release() {
        stop()
        synchronized(processorLock) {
            frameProcessor?.stop()
        }
        cameraThread.quitSafely()
    }

    fun setFrameProcessor(processor: FrameProcessor) {
        synchronized(processorLock) {
            frameProcessor?.stop()
            frameProcessor = processor
        }
    }

    // ------------------------------------
    // Camera lifecycle
    // ------------------------------------

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCaptureSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e(TAG, "Camera error: $error")
            camera.close()
            cameraDevice = null
        }
    }

    private fun createCaptureSession() {
        val surface = imageReader!!.surface

        cameraDevice?.createCaptureSession(
            listOf(surface),
            object : CameraCaptureSession.StateCallback() {

                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session

                    val request = cameraDevice!!.createCaptureRequest(
                        CameraDevice.TEMPLATE_PREVIEW
                    ).apply {
                        addTarget(surface)

                        set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO
                        )
                    }

                    session.setRepeatingRequest(
                        request.build(),
                        null,
                        cameraHandler
                    )
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "Capture session configuration failed")
                }
            },
            cameraHandler
        )
    }

    private fun setRotation(characteristics: CameraCharacteristics) {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val deviceRotation = windowManager.defaultDisplay.rotation

        val deviceDegrees = when (deviceRotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }

        val sensorOrientation =
            characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

        rotationDegrees = (sensorOrientation - deviceDegrees + 360) % 360
    }

    // ------------------------------------
    // Frame Processing
    // ------------------------------------

    private inner class FrameProcessingRunnable : Runnable {

        private val lock = Object()
        private var active = true
        private var pendingImage: Image? = null

        fun setActive(active: Boolean) {
            synchronized(lock) {
                this.active = active
                lock.notifyAll()
            }
        }

        fun setNextFrame(image: Image) {
            synchronized(lock) {
                pendingImage?.close()
                pendingImage = image
                lock.notifyAll()
            }
        }

        override fun run() {
            while (true) {
                val image: Image

                synchronized(lock) {
                    while (active && pendingImage == null) {
                        lock.wait()
                    }
                    if (!active) return

                    image = pendingImage!!
                    pendingImage = null
                }

                try {
                    val buffer = image.planes[0].buffer

                    val metadata = FrameMetadata(
                        previewSize!!.width,
                        previewSize!!.height,
                        rotationDegrees
                    )

                    synchronized(processorLock) {
                        frameProcessor?.process(buffer, metadata, graphicOverlay)
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Frame processing error", e)
                } finally {
                    image.close()
                }
            }
        }
    }

    // ------------------------------------
    // Size selection (Camera2)
    // ------------------------------------

    private fun selectSizePair(
        characteristics: CameraCharacteristics
    ): CameraSizePair? {

        val validSizes = Utils.generateValidPreviewSizeList(characteristics)

        var selected: CameraSizePair? = null
        var minDiff = Float.MAX_VALUE

        for (pair in validSizes) {
            val ratio = pair.preview.width.toFloat() / pair.preview.height
            val diff = abs(1.77f - ratio) // ~16:9

            if (diff < minDiff) {
                minDiff = diff
                selected = pair
            }
        }

        return selected
    }
}