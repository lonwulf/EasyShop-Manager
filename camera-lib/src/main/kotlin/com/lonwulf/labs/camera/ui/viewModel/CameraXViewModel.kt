package com.lonwulf.labs.camera.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.lonwulf.labs.camera.domain.model.DetectedObjectInfo
import com.lonwulf.labs.camera.domain.model.Product
import com.lonwulf.labs.camera.domain.model.SearchedObject
import com.lonwulf.labs.camera.util.PreferenceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** View model for handling application workflow based on camera preview.  */
class CameraXViewModel(application: Application) : AndroidViewModel(application) {

    private val _workflowState = MutableStateFlow(WorkflowState.NOT_STARTED)
    val workflowState: StateFlow<WorkflowState> = _workflowState.asStateFlow()

    private val _objectToSearch = MutableStateFlow<DetectedObjectInfo?>(null)
    val objectToSearch: StateFlow<DetectedObjectInfo?> = _objectToSearch.asStateFlow()

    private val _searchedObject = MutableStateFlow<SearchedObject?>(null)
    val searchedObject: StateFlow<SearchedObject?> = _searchedObject.asStateFlow()

    private var _detectedBarcode = MutableStateFlow<Barcode?>(null)
    val detectedBarcode: StateFlow<Barcode?> = _detectedBarcode.asStateFlow()

    private val objectIdsToSearch = HashSet<Int>()

    private val _isCameraLive = MutableStateFlow(false)
    val isCameraLive: StateFlow<Boolean> = _isCameraLive.asStateFlow()

    private var confirmedObject: DetectedObjectInfo? = null

    private val context: Context
        get() = getApplication<Application>().applicationContext

    /**
     * State set of the application workflow.
     */
    enum class WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED
    }

    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        if (workflowState != WorkflowState.CONFIRMED &&
            workflowState != WorkflowState.SEARCHING &&
            workflowState != WorkflowState.SEARCHED
        ) {
            confirmedObject = null
        }
        _workflowState.value = workflowState
    }

    @MainThread
    fun confirmingObject(confirmingObject: DetectedObjectInfo, progress: Float) {
        val isConfirmed = progress.compareTo(1f) == 0
        if (isConfirmed) {
            confirmedObject = confirmingObject
            if (PreferenceUtils.isAutoSearchEnabled(context)) {
                setWorkflowState(WorkflowState.SEARCHING)
                triggerSearch(confirmingObject)
            } else {
                setWorkflowState(WorkflowState.CONFIRMED)
            }
        } else {
            setWorkflowState(WorkflowState.CONFIRMING)
        }
    }

    @MainThread
    fun onSearchButtonClicked() {
        confirmedObject?.let {
            setWorkflowState(WorkflowState.SEARCHING)
            triggerSearch(it)
        }
    }

    private fun triggerSearch(detectedObject: DetectedObjectInfo) {
        val objectId = detectedObject.objectId ?: throw NullPointerException()
        if (objectIdsToSearch.contains(objectId)) {
            // Already in searching.
            return
        }

        objectIdsToSearch.add(objectId)
        _objectToSearch.value = detectedObject
    }

    fun markCameraLive() {
        _isCameraLive.value = true
        objectIdsToSearch.clear()
        _objectToSearch.value = null
        _searchedObject.value = null
        _detectedBarcode.value = null
    }

    fun markCameraFrozen() {
        _isCameraLive.value = false
    }

    @MainThread
    fun setDetectedBarcode(barcode: Barcode?) {
        _detectedBarcode.value = barcode
        setWorkflowState(if (barcode == null) WorkflowState.DETECTING else WorkflowState.DETECTED)
    }

    fun onSearchCompleted(detectedObject: DetectedObjectInfo, products: List<Product>) {
        val lConfirmedObject = confirmedObject
        if (detectedObject != lConfirmedObject) {
            // Drops the search result from the object that has lost focus.
            return
        }

        objectIdsToSearch.remove(detectedObject.objectId)
        setWorkflowState(WorkflowState.SEARCHED)

        _searchedObject.value = SearchedObject(context.resources, lConfirmedObject, products)
    }
}