package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.domain.model.Category
import com.lonwulf.labs.easyshopmanager.domain.model.Product
import com.lonwulf.labs.easyshopmanager.domain.model.SubCategory
import com.lonwulf.labs.easyshopmanager.domain.repository.ISQLRepository

class ProductsUseCase(private val sqlRepository: ISQLRepository) {

    operator fun invoke() = sqlRepository.getAllProducts()

//    suspend fun insertProduct(product: Product): Long = sqlRepository.insertProduct(product)

//    fun fetchAndInsertProducts(products: List<Product>) = sqlRepository.insertAllProducts(products)

    suspend fun fetchAndInsertCategories(categories: List<Category>) = sqlRepository.insertAllCategories(categories)
    suspend fun fetchAndInsertSubCategories(subCategories: List<SubCategory>) = sqlRepository.insertAllSubCategories(subCategories)
    suspend fun deleteProduct(id: String) = sqlRepository.deleteProduct(id)
}