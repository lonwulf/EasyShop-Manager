package com.lonwulf.labs.easyshopmanager.domain.uiState

import com.lonwulf.labs.easyshopmanager.core.domain.model.SubCategory

data class SubCategoriesState(
    val subCategories: List<SubCategory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
