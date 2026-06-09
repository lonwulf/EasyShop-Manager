package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.core.network.APIResult
import com.lonwulf.labs.easyshopmanager.core.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.core.domain.model.Resource
import com.lonwulf.labs.easyshopmanager.domain.repository.IAPIRepository
import com.lonwulf.labs.easyshopmanager.core.domain.repository.ISQLRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchAndInsertBrandsUseCase(
    private val apiRepository: IAPIRepository,
    private val sqlRepository: ISQLRepository
) {

    operator fun invoke(): Flow<Resource<Pair<Int, Int>>> = flow {
        emit(Resource.Loading)
        when (val response = apiRepository.fetchBrands()) {
            is APIResult.Loading -> {}
            is APIResult.Failure -> emit(
                Resource.Failure(
                    message = "${response.errorCode}: ${response.errorMessage} / ${response.cause.name.uppercase()}"
                        ?: "Network fetch failed"
                )
            )

            is APIResult.Success -> {
                val subCategories = response.value
                if (subCategories.isNullOrEmpty().not()) {
                    when (val insertOp = sqlRepository.insertBrands(subCategories)) {
                        is CacheResult.Success -> emit(Resource.Success(insertOp.data))
                        is CacheResult.Error -> emit(Resource.Failure(insertOp.msg, insertOp.cause))
                    }
                } else {
                    emit(Resource.Failure("Brands data empty or null from API"))
                }
            }
        }
    }
}