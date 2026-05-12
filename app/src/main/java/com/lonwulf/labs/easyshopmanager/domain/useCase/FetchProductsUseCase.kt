package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.domain.repository.ISQLRepository
import com.lonwulf.labs.easyshopmanager.domain.uiState.ProductState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class FetchProductsUseCase(private val sqlRepository: ISQLRepository) {

    operator fun invoke(): Flow<ProductState> =
        sqlRepository.getAllProducts()
            .map { cacheResult ->
                when (cacheResult) {
                    is CacheResult.Success -> ProductState(products = cacheResult.data)
                    is CacheResult.Error -> ProductState(error = cacheResult.msg)
                }
            }
            .onStart { emit(ProductState(isLoading = true)) }
            .catch { ex -> emit(ProductState(error = ex.message ?: "Unknown error")) }
}