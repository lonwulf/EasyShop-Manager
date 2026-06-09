package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.core.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.core.domain.model.Category
import com.lonwulf.labs.easyshopmanager.core.domain.model.Product
import com.lonwulf.labs.easyshopmanager.core.domain.model.SubCategory
import com.lonwulf.labs.easyshopmanager.core.domain.repository.ISQLRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductsUseCase(private val sqlRepository: ISQLRepository) {

    operator fun invoke() = sqlRepository.getAllProducts()

    fun insertProduct(product: Product): Flow<CacheResult<Long>> = flow {
        emit(sqlRepository.insertProduct(product))
    }

    suspend fun fetchAndInsertProducts(products: List<Product>): CacheResult<Pair<Int, Int>> {
        return sqlRepository.insertAllProducts(products)
    }

    suspend fun fetchAndInsertCategories(categories: List<Category>) = sqlRepository.insertAllCategories(categories)
    suspend fun fetchAndInsertSubCategories(subCategories: List<SubCategory>) =
        sqlRepository.insertAllSubCategories(subCategories)

    suspend fun deleteProduct(id: Long) = sqlRepository.deleteProduct(id)
}