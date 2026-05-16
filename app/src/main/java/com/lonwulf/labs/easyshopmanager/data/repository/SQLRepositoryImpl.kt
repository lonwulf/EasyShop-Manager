package com.lonwulf.labs.easyshopmanager.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.data.dataStore.LocalDataSource
import com.lonwulf.labs.easyshopmanager.db.Catalogue
import com.lonwulf.labs.easyshopmanager.domain.model.Category
import com.lonwulf.labs.easyshopmanager.domain.model.Product
import com.lonwulf.labs.easyshopmanager.domain.model.SubCategory
import com.lonwulf.labs.easyshopmanager.domain.model.toDomain
import com.lonwulf.labs.easyshopmanager.domain.repository.ISQLRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SQLRepositoryImpl(private val catalogue: Catalogue) : ISQLRepository, LocalDataSource() {
    private val productCatalogueQueries by lazy {
        catalogue.productCatalogueQueries
    }
    private val categoryQueries by lazy {
        catalogue.categoryQueries
    }
    private val subCategoryQueries by lazy {
        catalogue.subCategoryQueries
    }

    private val brandQueries by lazy {
        catalogue.brandQueries
    }

    override suspend fun insertAllCategories(categories: List<Category>): CacheResult<Pair<Int, Int>> =
        safeCacheCall(Dispatchers.IO) {
            var count = 0
            categoryQueries.transactionWithResult {
                categories.forEach { category ->
                    categoryQueries.upsertCategory(category.id, category.name, category.image)
                    count++
                }
                Pair(categories.size, count)
            }
        }

    override suspend fun upsertCategory(id: Long, name: String, image: String): CacheResult<Long> =
        safeCacheCall(Dispatchers.IO) {
            categoryQueries.upsertCategory(id, name, image).await()
        }

    override fun getCategoryById(id: Long): Flow<CacheResult<Category?>> =
        safeCacheFlow(
            categoryQueries.getCategoryById(id)
                .asFlow()
                .mapToOneOrNull(Dispatchers.IO)
                .map { it?.toDomain() })


    override fun getAllCategories(): Flow<CacheResult<List<Category>>> =
        safeCacheFlow(
            categoryQueries.getAllCategories()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.map { category -> category.toDomain() } }
        )

    override suspend fun insertAllSubCategories(subCategories: List<SubCategory>): CacheResult<Pair<Int, Int>> =
        safeCacheCall(Dispatchers.IO) {
            var count = 0
            subCategoryQueries.transactionWithResult {
                subCategories.forEach { subCategory ->
                    subCategoryQueries.upsertSubCategory(subCategory.id, subCategory.name, subCategory.categoryId)
                    count++
                }
                Pair(subCategories.size, count)
            }
        }

    override suspend fun upsertSubCategory(id: Long, name: String, categoryId: Long): CacheResult<Long> =
        safeCacheCall(Dispatchers.IO) {
            subCategoryQueries.upsertSubCategory(id, name, categoryId).await()
        }


    override fun getAllSubCategories(): Flow<CacheResult<List<SubCategory>>> =
        safeCacheFlow(
            subCategoryQueries.getAllSubCategories()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.map { subCategory -> subCategory.toDomain() } }
        )


    override fun getSubCategoriesByCategoryId(categoryId: Long): Flow<CacheResult<List<SubCategory>>> =
        safeCacheFlow(
            subCategoryQueries.getSubCategoriesByCategoryId(categoryId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.map { subCategory -> subCategory.toDomain() } }
        )

    override fun getSubCategoryById(id: Long): Flow<CacheResult<SubCategory?>> =
        safeCacheFlow(
            subCategoryQueries.getSubCategoryById(id)
                .asFlow()
                .mapToOneOrNull(Dispatchers.IO)
                .map { it?.toDomain() }
        )


    override suspend fun upsertProduct(product: Product): CacheResult<Long> =
        safeCacheCall(Dispatchers.IO) {
            val brandId = resolveBrandId(product.brand)
            productCatalogueQueries.upsertProduct(
                name = product.name,
                description = product.description,
                category_id = product.categoryId,
                sub_category_id = product.subCategoryId,
                price = product.price,
                buying_price = product.buyingPrice,
                is_bundled = if (product.isBundled == true) 1L else 0L,
                quantity = product.quantity,
                image_url = product.imageUrl ?: "",
                serial_no = product.serialNo,
                brand_id = brandId,
                vendor = product.vendor,
                id = product.id ?: 0

            ).await()
        }

    override suspend fun updateProduct(product: Product): CacheResult<Long> =
        safeCacheCall(Dispatchers.IO) {
            val brandId = resolveBrandId(product.brand)
            productCatalogueQueries.updateProduct(
                name = product.name,
                description = product.description,
                category_id = product.categoryId,
                sub_category_id = product.subCategoryId,
                price = product.price,
                buying_price = product.buyingPrice,
                is_bundled = if (product.isBundled == true) 1L else 0L,
                quantity = product.quantity,
                image_url = product.imageUrl ?: "",
                serial_no = product.serialNo,
                brand_id = brandId,
                vendor = product.vendor,
                id = product.id ?: 0
            ).await()
        }

    override suspend fun insertProduct(product: Product): CacheResult<Long> =
        safeCacheCall(Dispatchers.IO) {
            val brandId = resolveBrandId(product.brand)
            productCatalogueQueries.insertProduct(
                name = product.name,
                description = product.description,
                category_id = product.categoryId,
                sub_category_id = product.subCategoryId,
                price = product.price,
                buying_price = product.buyingPrice,
                is_bundled = if (product.isBundled == true) 1L else 0L,
                quantity = product.quantity,
                image_url = product.imageUrl ?: "",
                serial_no = product.serialNo,
                brand_id = brandId,
                vendor = product.vendor
            ).await()
        }

    override suspend fun insertAllProducts(products: List<Product>): CacheResult<Pair<Int, Int>> =
        safeCacheCall(Dispatchers.IO) {
            productCatalogueQueries.transactionWithResult {
                var count = 0
                products.forEach { product ->
                    val brandId = resolveBrandId(product.brand)
                    productCatalogueQueries.upsertProduct(
                        name = product.name,
                        description = product.description,
                        category_id = product.categoryId,
                        sub_category_id = product.subCategoryId,
                        price = product.price,
                        buying_price = product.buyingPrice,
                        is_bundled = if (product.isBundled == true) 1L else 0L,
                        quantity = product.quantity,
                        image_url = product.imageUrl ?: "",
                        serial_no = product.serialNo,
                        brand_id = brandId,
                        vendor = product.vendor,
                        id = product.id
                    )
                    count++
                }
                Pair(products.size, count)
            }
        }

    override fun getAllProducts(): Flow<CacheResult<List<Product>>> =
        safeCacheFlow(
            productCatalogueQueries.productWithRelations()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.map { product -> product.toDomain() } })


    override fun getProductById(id: Long): Flow<CacheResult<Product?>> =
        safeCacheFlow(
            productCatalogueQueries.getProductById(id)
                .asFlow()
                .mapToOneOrNull(Dispatchers.IO)
                .map { it?.toDomain() })


    override suspend fun deleteProduct(id: Long): CacheResult<Long> = safeCacheCall(Dispatchers.IO) {
        productCatalogueQueries.deleteProduct(id).await()
    }

    override fun getProductsWithCategory(): Flow<CacheResult<List<Product>>> =
        safeCacheFlow(
            productCatalogueQueries.selectProductsWithCategory()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.map { product -> product.toDomain() } }
        )

    override suspend fun getProductsWithCategoryOnce(): CacheResult<List<Product>> =
        safeCacheCall(Dispatchers.IO) {
            productCatalogueQueries.selectProductsWithCategory()
                .executeAsList()
                .map { it.toDomain() }
        }

    override fun getProductsByBrand(brandId: Long): Flow<CacheResult<List<Product>>> =
        safeCacheFlow(
            productCatalogueQueries.selectProductsByBrand(brandId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.map { product -> product.toDomain() } }
        )

    //TODO: fix potential hazard (db lock)
    private fun resolveBrandId(brandName: String?): Long? {
        if (brandName == null) return null
        return brandQueries.selectBrandIdByName(brandName).executeAsOneOrNull()
    }
}