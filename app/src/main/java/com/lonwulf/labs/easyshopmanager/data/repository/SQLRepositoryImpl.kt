package com.lonwulf.labs.easyshopmanager.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.data.util.LocalDataSource
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

    override suspend fun insertAllCategories(categories: List<Category>): CacheResult<Pair<Int, Int>> =
        safeCacheCall(Dispatchers.IO) {
            var count = 0
            categoryQueries.transactionWithResult {
                categories.forEach { category ->
                    categoryQueries.upsertCategory(category.id, category.name)
                    count++
                }
                Pair(categories.size, count)
            }
        }

    override suspend fun upsertCategory(id: String, name: String): CacheResult<Long> = safeCacheCall(Dispatchers.IO) {
        categoryQueries.upsertCategory(id, name).await()
    }

    override fun getCategoryById(id: String): Flow<CacheResult<Category?>> =
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

    override suspend fun upsertSubCategory(id: String, name: String, categoryId: String): CacheResult<Long> =
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


    override fun getSubCategoriesByCategoryId(categoryId: String): Flow<CacheResult<List<SubCategory>>> =
        safeCacheFlow(
            subCategoryQueries.getSubCategoriesByCategoryId(categoryId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.map { subCategory -> subCategory.toDomain() } }
        )

    override fun getSubCategoryById(id: String): Flow<CacheResult<SubCategory?>> =
        safeCacheFlow(
            subCategoryQueries.getSubCategoryById(id)
                .asFlow()
                .mapToOneOrNull(Dispatchers.IO)
                .map { it?.toDomain() }
        )


    override suspend fun upsertProduct(product: Product): CacheResult<Long> = safeCacheCall(Dispatchers.IO) {
        productCatalogueQueries.upsertProduct(
            product.id,
            product.name,
            product.description,
            product.categoryId,
            product.subCategoryId,
            product.price,
            if (product.isBundled) 1L else 0L,
            product.quantity,
            product.imageUrl,
            product.serialNo,
            product.brand
        ).await()
    }

    override suspend fun updateProduct(
        name: String,
        price: Double,
        subCategoryId: String,
        isBundled: Boolean,
        quantity: Long,
        productId: String
    ): CacheResult<Long> = safeCacheCall(Dispatchers.IO) {
        productCatalogueQueries.updateProduct(
            name,
            price,
            subCategoryId,
            if (isBundled) 1L else 0L, quantity, productId
        ).await()
    }

    override suspend fun insertProduct(product: Product): CacheResult<Long> = safeCacheCall(Dispatchers.IO) {
        productCatalogueQueries.insertProduct(
            id = product.id,
            name = product.name,
            description = product.description,
            category_id = product.categoryId,
            sub_category_id = product.subCategoryId,
            price = product.price,
            is_bundled = if (product.isBundled) 1L else 0L,
            quantity = product.quantity,
            image_url = product.imageUrl,
            serial_no = product.serialNo,
            brand = product.brand,
        ).await()
    }

    override suspend fun insertAllProducts(products: List<Product>): CacheResult<Pair<Int, Int>> =
        safeCacheCall(Dispatchers.IO) {
            productCatalogueQueries.transactionWithResult {
                var count = 0
                products.forEach { product ->
                    productCatalogueQueries.upsertProduct(
                        id = product.id,
                        name = product.name,
                        description = product.description,
                        category_id = product.categoryId,
                        sub_category_id = product.subCategoryId,
                        price = product.price,
                        is_bundled = if (product.isBundled) 1L else 0L,
                        quantity = product.quantity,
                        image_url = product.imageUrl,
                        seral_no = product.serialNo,
                        brand = product.brand,
                    )
                    count++
                }
                Pair(products.size, count)
            }
        }

    override fun getAllProducts(): Flow<CacheResult<List<Product>>> =
        safeCacheFlow(
            productCatalogueQueries.getAllProducts()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.map { product -> product.toDomain() } })


    override fun getProductById(id: String): Flow<CacheResult<Product?>> =
        safeCacheFlow(
            productCatalogueQueries.getProductById(id)
                .asFlow()
                .mapToOneOrNull(Dispatchers.IO)
                .map { it?.toDomain() })


    override suspend fun deleteProduct(id: String): CacheResult<Long> = safeCacheCall(Dispatchers.IO) {
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

    override fun getProductsByBrand(brand: String): Flow<CacheResult<List<Product>>> =
        safeCacheFlow(
            productCatalogueQueries.selectProductsByBrand(brand)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.map { product -> product.toDomain() } }
        )
}