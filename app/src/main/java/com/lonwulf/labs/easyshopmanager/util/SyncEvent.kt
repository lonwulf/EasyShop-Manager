package com.lonwulf.labs.easyshopmanager.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncEvent {

    enum class SyncState {
        IDLE,
        SYNCING,
        COMPLETED
    }

    private val _syncState = MutableStateFlow(SyncState.IDLE)

    val syncState
        get() = _syncState.asStateFlow()

    fun startSync() {
        _syncState.value = SyncState.SYNCING
    }

    fun endSync() {
        _syncState.value = SyncState.COMPLETED
    }

    fun resetSync() {
        _syncState.value = SyncState.IDLE
    }
}