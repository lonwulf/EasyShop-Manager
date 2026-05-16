package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.data.network.APIResult
import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.domain.model.Resource
import com.lonwulf.labs.easyshopmanager.domain.repository.IAPIRepository
import com.lonwulf.labs.easyshopmanager.domain.repository.ISQLRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchAndInsertSubCategoriesUseCase(
    private val sqlRepository: ISQLRepository,
    private val apiRepository: IAPIRepository
) {
    operator fun invoke(): Flow<Resource<Pair<Int, Int>>> = flow {
        emit(Resource.Loading)
        when (val response = apiRepository.fetchSubCategories()) {
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
                    when (val insertOp = sqlRepository.insertAllSubCategories(subCategories)) {
                        is CacheResult.Success -> emit(Resource.Success(insertOp.data))
                        is CacheResult.Error -> emit(Resource.Failure(insertOp.msg, insertOp.cause))
                    }
                } else {
                    emit(Resource.Failure("SubCategories data empty or null from API"))
                }
            }
        }
    }
}