package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.core.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.core.domain.model.Brand
import com.lonwulf.labs.easyshopmanager.core.domain.model.Resource
import com.lonwulf.labs.easyshopmanager.core.domain.repository.ISQLRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class BrandsUseCase(private val sqlRepository: ISQLRepository) {

    operator fun invoke(): Flow<Resource<List<Brand>>> =
        sqlRepository.getAllBrands()
            .map { cacheResult ->
                when (cacheResult) {
                    is CacheResult.Error -> Resource.Failure(cacheResult.msg, cacheResult.cause)
                    is CacheResult.Success -> Resource.Success(cacheResult.data)
                }
            }
            .onStart { emit(Resource.Loading) }
            .catch { ex -> emit(Resource.Failure(ex.message ?: "Unknown error")) }
}