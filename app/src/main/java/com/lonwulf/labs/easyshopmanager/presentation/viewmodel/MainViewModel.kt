package com.lonwulf.labs.easyshopmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.lonwulf.labs.easyshopmanager.domain.uiState.ProductState
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    var productsState = MutableStateFlow(ProductState())
        private set
}