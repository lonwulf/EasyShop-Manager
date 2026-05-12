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

package com.lonwulf.labs.camera.source.repository

import android.annotation.SuppressLint
import android.os.SystemClock
import android.util.Log
import androidx.annotation.GuardedBy
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.lonwulf.labs.camera.ScopedExecutor
import com.lonwulf.labs.camera.domain.model.CameraInputInfo
import com.lonwulf.labs.camera.domain.model.FrameMetadata
import com.lonwulf.labs.camera.domain.model.InputInfo
import com.lonwulf.labs.camera.domain.repository.FrameProcessor
import com.lonwulf.labs.camera.ui.camera.GraphicOverlay

/** Abstract base class of [FrameProcessor].  */
abstract class FrameProcessorBase<T> : FrameProcessor {

    // To keep the latest frame and its metadata.
    @GuardedBy("this")
    private var latestFrame: ImageProxy? = null

    // To keep the frame and metadata in process.
    @GuardedBy("this")
    private var processingFrame: ImageProxy? = null

    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    @Synchronized
    override fun process(
        imageProxy: ImageProxy,
        graphicOverlay: GraphicOverlay
    ) {
        latestFrame?.close()
        latestFrame = imageProxy
        if (processingFrame == null) {
            processLatestFrame(graphicOverlay)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Synchronized
    private fun processLatestFrame(graphicOverlay: GraphicOverlay) {
        processingFrame = latestFrame
        latestFrame = null
        val frame = processingFrame ?: return

        val mediaImage = frame.image
        if (mediaImage == null) {
            frame.close()
            processingFrame = null
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, frame.imageInfo.rotationDegrees)
        val frameMetaData = FrameMetadata(
            frame.width,
            frame.height,
            frame.imageInfo.rotationDegrees,
            false // Back camera is not flipped usually
        )

        val startMs = SystemClock.elapsedRealtime()
        detectInImage(image)
            .addOnSuccessListener(executor) { results: T ->
                Log.d(TAG, "Latency is: ${SystemClock.elapsedRealtime() - startMs}")

                // Update graphic overlay with latest frame metadata for proper scaling
                graphicOverlay.setFrameMetadata(frameMetaData)

                val inputInfo = CameraInputInfo(
                    mediaImage,
                    frame.imageInfo.rotationDegrees
                )

                this@FrameProcessorBase.onSuccess(inputInfo, results, graphicOverlay)
                
                frame.close()
                processingFrame = null
                processLatestFrame(graphicOverlay)
            }
            .addOnFailureListener(executor) { e ->
                this@FrameProcessorBase.onFailure(e)
                frame.close()
                processingFrame = null
                processLatestFrame(graphicOverlay)
            }
    }

    override fun stop() {
        executor.shutdown()
        synchronized(this) {
            latestFrame?.close()
            latestFrame = null
            processingFrame?.close()
            processingFrame = null
        }
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>

    /** Be called when the detection succeeds.  */
    protected abstract fun onSuccess(
        inputInfo: InputInfo,
        results: T,
        graphicOverlay: GraphicOverlay
    )

    protected abstract fun onFailure(e: Exception)

    companion object {
        private const val TAG = "FrameProcessorBase"
    }
}
