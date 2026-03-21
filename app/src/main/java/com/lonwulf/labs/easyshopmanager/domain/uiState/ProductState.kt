package com.lonwulf.labs.easyshopmanager.domain.uiState

import com.lonwulf.labs.easyshopmanager.domain.model.Product

data class ProductState(
    val products:List<Product> = emptyList(),
    val isLoading:Boolean = false,
    val error:String? = null,
)
