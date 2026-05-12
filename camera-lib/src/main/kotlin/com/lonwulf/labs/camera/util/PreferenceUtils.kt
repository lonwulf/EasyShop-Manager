package com.lonwulf.labs.camera.util

import android.content.Context
import android.graphics.RectF
import android.preference.PreferenceManager
import androidx.annotation.StringRes
import com.google.mlkit.vision.barcode.common.Barcode
import com.lonwulf.labs.camera.ui.camera.GraphicOverlay
import com.lonwulf.labs.camera_lib.R

object PreferenceUtils {

    private fun getBooleanPref(
        context: Context,
        @StringRes prefKeyId: Int,
        defaultValue: Boolean
    ): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(prefKeyId), defaultValue)

    private fun getIntPref(context: Context, @StringRes prefKeyId: Int, defaultValue: Int): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(prefKeyId)
        return try {
            sharedPreferences.getString(prefKey, null)?.toInt() ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun isAutoSearchEnabled(context: Context): Boolean =
        getBooleanPref(context, R.string.pref_key_enable_auto_search, true)

    fun isMultipleObjectsMode(context: Context): Boolean =
        getBooleanPref(
            context,
            R.string.pref_key_object_detector_enable_multiple_objects,
            false
        )

    fun isClassificationEnabled(context: Context): Boolean =
        getBooleanPref(
            context,
            R.string.pref_key_object_detector_enable_classification,
            false
        )

    fun getBarcodeReticleBox(overlay: GraphicOverlay): RectF {
        val context = overlay.context
        val overlayWidth = overlay.width.toFloat()
        val overlayHeight = overlay.height.toFloat()
        val boxWidth =
            overlayWidth * getIntPref(context, R.string.pref_key_barcode_reticle_width, 80) / 100
        val boxHeight =
            overlayHeight * getIntPref(context, R.string.pref_key_barcode_reticle_height, 35) / 100
        val cx = overlayWidth / 2
        val cy = overlayHeight / 2
        return RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2)
    }

    fun getProgressToMeetBarcodeSizeRequirement(overlay: GraphicOverlay, barcode: Barcode): Float {
        val context = overlay.context
        return if (getBooleanPref(
                context,
                R.string.pref_key_enable_barcode_size_check,
                false
            )
        ) {
            val reticleBoxWidth = getBarcodeReticleBox(overlay).width()
            val barcodeBoundingBox = barcode.boundingBox ?: return 0f
            val barcodeWidth = overlay.translateX(barcodeBoundingBox.width().toFloat())
            val requiredWidth =
                reticleBoxWidth * getIntPref(
                    context,
                    R.string.pref_key_minimum_barcode_width,
                    50
                ) / 100
            (barcodeWidth / requiredWidth).coerceAtMost(1f)
        } else {
            1f
        }
    }

    fun shouldDelayLoadingBarcodeResult(context: Context): Boolean =
        getBooleanPref(
            context,
            R.string.pref_key_delay_loading_barcode_result,
            true
        )

    fun getConfirmationTimeMs(context: Context): Int =
        when {
            isMultipleObjectsMode(context) -> 300
            isAutoSearchEnabled(context) -> getIntPref(
                context,
                R.string.pref_key_confirmation_time_in_auto_search,
                1500
            )

            else -> getIntPref(
                context,
                R.string.pref_key_confirmation_time_in_manual_search,
                500
            )
        }
}