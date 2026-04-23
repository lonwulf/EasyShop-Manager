package com.lonwulf.labs.easyshopmanager.domain.repository

import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.domain.model.Category
import com.lonwulf.labs.easyshopmanager.domain.model.Product
import com.lonwulf.labs.easyshopmanager.domain.model.SubCategory
import kotlinx.coroutines.flow.Flow

interface ISQLRepository {
    suspend fun insertAllCategories(categories: List<Category>): CacheResult<Pair<Int, Int>>
    suspend fun upsertCategory(id: String, name: String): CacheResult<Long>
    fun getCategoryById(id: String): Flow<CacheResult<Category?>>
    fun getAllCategories(): Flow<CacheResult<List<Category>>>

    suspend fun insertAllSubCategories(subCategories: List<SubCategory>): CacheResult<Pair<Int, Int>>
    suspend fun upsertSubCategory(id: String, name: String, categoryId: String): CacheResult<Long>
    fun getAllSubCategories(): Flow<CacheResult<List<SubCategory>>>
    fun getSubCategoriesByCategoryId(categoryId: String): Flow<CacheResult<List<SubCategory>>>
    fun getSubCategoryById(id: String): Flow<CacheResult<SubCategory?>>

    suspend fun upsertProduct(product: Product): CacheResult<Long>
    suspend fun updateProduct(
        name: String,
        price: Double,
        subCategoryId: String,
        isBundled: Boolean,
        quantity: Long,
        productId: String
    ): CacheResult<Long>

    suspend fun insertProduct(product: Product): CacheResult<Long>
    suspend fun insertAllProducts(products: List<Product>): CacheResult<Pair<Int, Int>>
    fun getAllProducts(): Flow<CacheResult<List<Product>>>
    fun getProductById(id: String): Flow<CacheResult<Product?>>
    suspend fun deleteProduct(id: String): CacheResult<Long>
    fun getProductsWithCategory(): Flow<CacheResult<List<Product>>>
    suspend fun getProductsWithCategoryOnce(): CacheResult<List<Product>>

}