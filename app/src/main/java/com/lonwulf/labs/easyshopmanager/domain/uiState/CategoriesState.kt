package com.lonwulf.labs.easyshopmanager.domain.uiState

import com.lonwulf.labs.easyshopmanager.domain.model.Category

data class CategoriesState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
