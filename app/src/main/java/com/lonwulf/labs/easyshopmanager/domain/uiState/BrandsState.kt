package com.lonwulf.labs.easyshopmanager.domain.uiState

import com.lonwulf.labs.easyshopmanager.core.domain.model.Brand

data class BrandsState(
    val brands: List<Brand> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
