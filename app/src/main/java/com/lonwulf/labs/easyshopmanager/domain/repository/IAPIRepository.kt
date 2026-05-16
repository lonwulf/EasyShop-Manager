package com.lonwulf.labs.easyshopmanager.domain.repository

import com.lonwulf.labs.easyshopmanager.data.network.APIResult
import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.domain.model.Category
import com.lonwulf.labs.easyshopmanager.domain.model.SubCategory

interface IAPIRepository {
    suspend fun fetchCategories(): APIResult<List<Category>?>
    suspend fun fetchSubCategories(): APIResult<List<SubCategory>?>
}