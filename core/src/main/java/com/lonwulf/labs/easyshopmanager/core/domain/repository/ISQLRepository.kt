package com.lonwulf.labs.easyshopmanager.core.domain.repository

import com.lonwulf.labs.easyshopmanager.core.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.core.domain.model.Brand
import com.lonwulf.labs.easyshopmanager.core.domain.model.Category
import com.lonwulf.labs.easyshopmanager.core.domain.model.Product
import com.lonwulf.labs.easyshopmanager.core.domain.model.SubCategory
import kotlinx.coroutines.flow.Flow

interface ISQLRepository {
    suspend fun insertAllCategories(categories: List<Category>): CacheResult<Pair<Int, Int>>
    suspend fun upsertCategory(id: Long, name: String, image: String): CacheResult<Long>
    fun getCategoryById(id: Long): Flow<CacheResult<Category?>>
    fun getAllCategories(): Flow<CacheResult<List<Category>>>

    suspend fun insertAllSubCategories(subCategories: List<SubCategory>): CacheResult<Pair<Int, Int>>
    suspend fun upsertSubCategory(id: Long, name: String, categoryId: Long, imageUrl: String): CacheResult<Long>
    fun getAllSubCategories(): Flow<CacheResult<List<SubCategory>>>
    fun getSubCategoriesByCategoryId(categoryId: Long): Flow<CacheResult<List<SubCategory>>>
    fun getSubCategoryById(id: Long): Flow<CacheResult<SubCategory?>>

    suspend fun upsertProduct(product: Product): CacheResult<Long>
    suspend fun updateProduct(product: Product): CacheResult<Long>

    suspend fun insertProduct(product: Product): CacheResult<Long>
    suspend fun insertAllProducts(products: List<Product>): CacheResult<Pair<Int, Int>>
    fun getAllProducts(): Flow<CacheResult<List<Product>>>
    fun getProductById(id: Long): Flow<CacheResult<Product?>>
    suspend fun deleteProduct(id: Long): CacheResult<Long>
    fun getProductsWithCategory(): Flow<CacheResult<List<Product>>>
    suspend fun getProductsWithCategoryOnce(): CacheResult<List<Product>>
    fun getProductsByBrand(brandId: Long): Flow<CacheResult<List<Product>>>

    suspend fun insertBrands(brands: List<Brand>): CacheResult<Pair<Int, Int>>
    fun getAllBrands(): Flow<CacheResult<List<Brand>>>
}