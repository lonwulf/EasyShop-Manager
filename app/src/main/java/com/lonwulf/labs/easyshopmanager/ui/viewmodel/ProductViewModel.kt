package com.lonwulf.labs.easyshopmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonwulf.labs.easyshopmanager.domain.useCase.ProductsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(private val productsUseCase: ProductsUseCase) : ViewModel() {



    fun deleteProduct(id: String) = viewModelScope.launch(Dispatchers.IO) {
        productsUseCase.deleteProduct(id)
    }
}