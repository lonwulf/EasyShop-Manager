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

    var previewSize: Size? = null
        private set

    private var rotationDegrees: Int = 0

    private val processorLock = Any()
    private var frameProcessor: FrameProcessor? = null

    private val processingRunnable = FrameProcessingRunnable()
    private var processingThread: Thread? = null

    private val cameraThread = HandlerThread("CameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)

    fun setFrameProcessor(processor: FrameProcessor) {
        synchronized(processorLock) {
            frameProcessor?.stop()
            frameProcessor = processor
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    fun start(previewSurface: Surface) {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)

        previewSize = selectSize(characteristics)

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

        cameraManager.openCamera(
            cameraId,
            stateCallback(previewSurface),
            cameraHandler
        )

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

    private fun stateCallback(previewSurface: Surface) =
        object : CameraDevice.StateCallback() {

            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                createSession(previewSurface)
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

    private fun createSession(previewSurface: Surface) {
        val imageSurface = imageReader!!.surface

        cameraDevice?.createCaptureSession(
            listOf(previewSurface, imageSurface),
            object : CameraCaptureSession.StateCallback() {

                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session

                    val request = cameraDevice!!.createCaptureRequest(
                        CameraDevice.TEMPLATE_PREVIEW
                    ).apply {
                        addTarget(previewSurface)
                        addTarget(imageSurface)

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
                    Log.e(TAG, "Session config failed")
                }
            },
            cameraHandler
        )
    }

    private fun setRotation(characteristics: CameraCharacteristics) {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val rotation = windowManager.defaultDisplay.rotation

        val deviceDegrees = when (rotation) {
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
                    val nv21 = image.toNV21()

                    val metadata = FrameMetadata(
                        previewSize!!.width,
                        previewSize!!.height,
                        rotationDegrees
                    )

                    synchronized(processorLock) {
                        frameProcessor?.process(nv21, metadata, graphicOverlay)
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Processing error", e)
                } finally {
                    image.close()
                }
            }
        }
    }

    private fun selectSize(characteristics: CameraCharacteristics): Size {
        val map = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!

        val sizes = map.getOutputSizes(ImageFormat.YUV_420_888)

        return sizes.minByOrNull {
            abs((it.width.toFloat() / it.height) - 1.77f)
        }!!
    }

    fun Image.toNV21(): ByteArray {
        val y = planes[0].buffer
        val u = planes[1].buffer
        val v = planes[2].buffer

        val ySize = y.remaining()
        val uSize = u.remaining()
        val vSize = v.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        y.get(nv21, 0, ySize)

        var offset = ySize

        for (i in 0 until uSize step 2) {
            nv21[offset++] = v.get(i)
            nv21[offset++] = u.get(i)
        }

        return nv21
    }
}