package com.lonwulf.labs.easyshopmanager.domain.repository

import com.lonwulf.labs.easyshopmanager.domain.model.Category
import com.lonwulf.labs.easyshopmanager.domain.model.Product
import com.lonwulf.labs.easyshopmanager.domain.model.SubCategory
import kotlinx.coroutines.flow.Flow

interface ISQLRepository {
    suspend fun upsertCategory(id: String, name: String)
    suspend fun getCategoryById(id: String): Category?
    fun getAllCategories(): Flow<List<Category>>
    suspend fun upsertSubCategory(id: String, name: String, categoryId: String)
    fun getAllSubCategories(): Flow<List<SubCategory>>
    fun getSubCategoriesByCategoryId(categoryId: String): Flow<List<SubCategory>>
    suspend fun upsertProduct(product: Product)
    fun getAllProducts(): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    suspend fun deleteProduct(id: String)

}