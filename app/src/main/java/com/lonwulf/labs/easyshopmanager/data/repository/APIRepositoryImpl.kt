package com.lonwulf.labs.easyshopmanager.data.repository

import com.lonwulf.labs.easyshopmanager.data.dto.toDomain
import com.lonwulf.labs.easyshopmanager.core.network.APIResult
import com.lonwulf.labs.easyshopmanager.data.source.remote.AppRemoteDataSource
import com.lonwulf.labs.easyshopmanager.core.domain.model.Brand
import com.lonwulf.labs.easyshopmanager.core.domain.model.Category
import com.lonwulf.labs.easyshopmanager.core.domain.model.SubCategory
import com.lonwulf.labs.easyshopmanager.domain.repository.IAPIRepository
import kotlinx.coroutines.Dispatchers

class APIRepositoryImpl(private val source: AppRemoteDataSource) : IAPIRepository {
    override suspend fun fetchCategories(): APIResult<List<Category>?> {
        return when (val response = source.fetchCategories(Dispatchers.IO)) {
            is APIResult.Loading -> APIResult.Loading
            is APIResult.Success -> {
                val result = response.value.data?.map { it.toDomain() }
                APIResult.Success(result)
            }

            is APIResult.Failure -> APIResult.Failure(response.errorCode, response.errorMessage, response.cause)

        }
    }

    override suspend fun fetchSubCategories(): APIResult<List<SubCategory>?> =
        when (val response = source.fetchSubCategories(Dispatchers.IO)) {
            is APIResult.Loading -> APIResult.Loading
            is APIResult.Success -> {
                val result = response.value.data?.map { it.toDomain() }
                APIResult.Success(result)
            }

            is APIResult.Failure -> APIResult.Failure(response.errorCode, response.errorMessage, response.cause)

        }

    override suspend fun fetchBrands(): APIResult<List<Brand>?> =
        when (val response = source.fetchBrands(Dispatchers.IO)) {
            is APIResult.Loading -> APIResult.Loading
            is APIResult.Success -> {
                val result = response.value.data?.map { it.toDomain() }
                APIResult.Success(result)
            }

            is APIResult.Failure -> APIResult.Failure(response.errorCode, response.errorMessage, response.cause)

        }
}