package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.core.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.core.domain.model.Category
import com.lonwulf.labs.easyshopmanager.core.domain.model.Resource
import com.lonwulf.labs.easyshopmanager.core.domain.model.SubCategory
import com.lonwulf.labs.easyshopmanager.core.domain.repository.ISQLRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CategoriesSubCategoriesUseCase(private val sqlRepository: ISQLRepository) {
    fun getCategories(): Flow<Resource<List<Category>>> =
        sqlRepository.getAllCategories()
            .map { cacheResult ->
                when (cacheResult) {
                    is CacheResult.Error -> Resource.Failure(message = cacheResult.msg)
                    is CacheResult.Success -> Resource.Success(data = cacheResult.data)
                }
            }
            .onStart { emit(Resource.Loading) }
            .catch { ex -> emit(Resource.Failure(ex.message ?: "Unknown error", ex)) }

    fun getSubCategories(): Flow<Resource<List<SubCategory>>> =
        sqlRepository.getAllSubCategories()
            .map { cacheResult ->
                when (cacheResult) {
                    is CacheResult.Error -> Resource.Failure(message = cacheResult.msg)
                    is CacheResult.Success -> Resource.Success(data = cacheResult.data)
                }
            }
            .onStart { emit(Resource.Loading) }
            .catch { ex -> emit(Resource.Failure(ex.message ?: "Unknown error", ex)) }


    fun getSubCategoriesByCategory(categoryId: Long): Flow<Resource<List<SubCategory>>> =
        sqlRepository.getSubCategoriesByCategoryId(categoryId)
            .map { cacheResult ->
                when (cacheResult) {
                    is CacheResult.Error -> Resource.Failure(message = cacheResult.msg)
                    is CacheResult.Success -> Resource.Success(data = cacheResult.data)
                }
            }
            .onStart { emit(Resource.Loading) }
            .catch { ex -> emit(Resource.Failure(ex.message ?: "Unknown error", ex)) }

}