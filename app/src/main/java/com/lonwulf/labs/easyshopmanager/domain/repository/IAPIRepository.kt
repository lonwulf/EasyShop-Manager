package com.lonwulf.labs.easyshopmanager.domain.repository

import com.lonwulf.labs.easyshopmanager.core.network.APIResult
import com.lonwulf.labs.easyshopmanager.core.domain.model.Brand
import com.lonwulf.labs.easyshopmanager.core.domain.model.Category
import com.lonwulf.labs.easyshopmanager.core.domain.model.SubCategory

interface IAPIRepository {
    suspend fun fetchCategories(): APIResult<List<Category>?>
    suspend fun fetchSubCategories(): APIResult<List<SubCategory>?>
    suspend fun fetchBrands(): APIResult<List<Brand>?>
}