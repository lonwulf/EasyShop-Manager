package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.data.network.APIResult
import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.domain.model.Resource
import com.lonwulf.labs.easyshopmanager.domain.repository.IAPIRepository
import com.lonwulf.labs.easyshopmanager.domain.repository.ISQLRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchAndInsertCategoriesUseCase(
    private val sqlRepository: ISQLRepository,
    private val apiRepository: IAPIRepository
) {
    operator fun invoke(): Flow<Resource<Pair<Int, Int>>> = flow {
        emit(Resource.Loading)
        when (val response = apiRepository.fetchCategories()) {
            is APIResult.Loading -> {}
            is APIResult.Failure -> emit(
                Resource.Failure(
                    message = "${response.errorCode}: ${response.errorMessage} / ${response.cause.name.uppercase()}"
                        ?: "Network fetch failed"
                )
            )

            is APIResult.Success -> {
                val categories = response.value
                if (categories.isNullOrEmpty().not()) {
                    when (val insertOp = sqlRepository.insertAllCategories(categories)) {
                        is CacheResult.Success -> emit(Resource.Success(insertOp.data))
                        is CacheResult.Error -> emit(Resource.Failure(insertOp.msg, insertOp.cause))
                    }
                } else {
                    emit(Resource.Failure("Categories data empty or null from API"))
                }
            }
        }
    }
}